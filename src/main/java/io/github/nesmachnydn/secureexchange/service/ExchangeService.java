package io.github.nesmachnydn.secureexchange.service;

import io.github.nesmachnydn.secureexchange.domain.TransferDirection;
import io.github.nesmachnydn.secureexchange.domain.TransferRecord;
import io.github.nesmachnydn.secureexchange.domain.TransferStatus;
import io.github.nesmachnydn.secureexchange.persistence.TransferEntity;
import io.github.nesmachnydn.secureexchange.persistence.TransferRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ExchangeService {
    private final ExchangePaths paths;
    private final Sha256IntegrityService integrity;
    private final FileNameSanitizer sanitizer;
    private final TransferRepository transfers;
    private final AuditService audit;

    public ExchangeService(ExchangePaths paths, Sha256IntegrityService integrity,
                           FileNameSanitizer sanitizer, TransferRepository transfers, AuditService audit) {
        this.paths = paths;
        this.integrity = integrity;
        this.sanitizer = sanitizer;
        this.transfers = transfers;
        this.audit = audit;
    }

    @Transactional
    public TransferRecord stageOutbound(String originalFileName, byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Empty files are not accepted");
        }

        UUID id = UUID.randomUUID();
        String safeName = sanitizer.sanitize(originalFileName);
        String storedName = id + "--" + safeName;
        String sha256 = integrity.digest(data);
        Path destination = paths.clientOut().resolve(storedName);
        Path temporary = paths.clientOut().resolve(storedName + ".part");

        Files.write(temporary, data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        moveAtomicallyWhenPossible(temporary, destination);
        writeChecksumSidecar(destination, sha256);
        Files.copy(destination, paths.clientArchiveOutbound().resolve(storedName),
                StandardCopyOption.REPLACE_EXISTING);

        TransferEntity entity = new TransferEntity(id, TransferDirection.OUTBOUND,
                TransferStatus.READY_FOR_TRANSPORT, safeName, storedName, sha256,
                data.length, Instant.now(), null, null);
        transfers.save(entity);
        audit.record("OUTBOUND_STAGED", id, "File registered with SHA-256 " + sha256);
        return entity.toRecord();
    }

    @Transactional(readOnly = true)
    public boolean verifyOutbound(UUID transferId) throws IOException {
        TransferEntity transfer = transfers.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown transfer: " + transferId));
        Path file = paths.clientOut().resolve(transfer.getStoredFileName());
        return Files.isRegularFile(file) && transfer.getSha256().equals(integrity.digest(file));
    }

    @Transactional
    public int processInbound() throws IOException {
        int processed = 0;
        try (var stream = Files.list(paths.clientIn())) {
            for (Path file : stream.filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().endsWith(".sha256"))
                    .toList()) {
                processInboundFile(file);
                processed++;
            }
        }
        return processed;
    }

    @Transactional(readOnly = true)
    public List<TransferRecord> listTransfers() {
        return transfers.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(TransferEntity::toRecord).toList();
    }

    private void processInboundFile(Path file) throws IOException {
        String storedName = file.getFileName().toString();
        String originalName = originalNameFromStored(storedName);
        Path sidecar = file.resolveSibling(storedName + ".sha256");
        String actual = integrity.digest(file);
        long size = Files.size(file);
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        if (!Files.isRegularFile(sidecar)) {
            quarantine(file, sidecar);
            saveInbound(id, originalName, storedName, actual, size, now,
                    TransferStatus.QUARANTINED, "Missing checksum sidecar");
            return;
        }

        String expected = parseExpectedChecksum(sidecar);
        if (!actual.equalsIgnoreCase(expected)) {
            quarantine(file, sidecar);
            saveInbound(id, originalName, storedName, actual, size, now,
                    TransferStatus.QUARANTINED, "SHA-256 mismatch; expected " + expected);
            return;
        }

        Path archived = paths.clientArchiveInbound().resolve(storedName);
        Files.move(file, archived, StandardCopyOption.REPLACE_EXISTING);
        Files.move(sidecar, archived.resolveSibling(storedName + ".sha256"),
                StandardCopyOption.REPLACE_EXISTING);
        saveInbound(id, originalName, storedName, actual, size, now,
                TransferStatus.VERIFIED, null);
    }

    private void saveInbound(UUID id, String originalName, String storedName, String hash,
                             long size, Instant now, TransferStatus status, String reason) {
        TransferEntity entity = new TransferEntity(id, TransferDirection.INBOUND, status,
                originalName, storedName, hash, size, now,
                status == TransferStatus.VERIFIED ? now : null, reason);
        transfers.save(entity);
        audit.record(status == TransferStatus.VERIFIED ? "INBOUND_VERIFIED" : "INBOUND_QUARANTINED",
                id, reason == null ? "Inbound SHA-256 verified: " + hash : reason);
    }

    private void quarantine(Path file, Path sidecar) throws IOException {
        Files.move(file, paths.clientQuarantine().resolve(file.getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
        if (Files.exists(sidecar)) {
            Files.move(sidecar, paths.clientQuarantine().resolve(sidecar.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String parseExpectedChecksum(Path sidecar) throws IOException {
        String content = Files.readString(sidecar, StandardCharsets.UTF_8).trim();
        if (content.length() < 64) {
            return "invalid";
        }
        return content.substring(0, 64).toLowerCase();
    }

    private String originalNameFromStored(String storedName) {
        int separator = storedName.indexOf("--");
        return separator >= 0 && separator + 2 < storedName.length()
                ? storedName.substring(separator + 2) : storedName;
    }

    private void writeChecksumSidecar(Path file, String sha256) throws IOException {
        String content = sha256 + "  " + file.getFileName() + System.lineSeparator();
        Files.writeString(file.resolveSibling(file.getFileName() + ".sha256"), content,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    private void moveAtomicallyWhenPossible(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target);
        }
    }
}
