package io.github.nesmachnydn.secureexchange.persistence;

import io.github.nesmachnydn.secureexchange.domain.TransferDirection;
import io.github.nesmachnydn.secureexchange.domain.TransferRecord;
import io.github.nesmachnydn.secureexchange.domain.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer_record")
public class TransferEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransferDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransferStatus status;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 320)
    private String storedFileName;

    @Column(nullable = false, length = 64)
    private String sha256;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant verifiedAt;

    @Column(length = 512)
    private String reason;

    protected TransferEntity() {
    }

    public TransferEntity(UUID id, TransferDirection direction, TransferStatus status,
                          String originalFileName, String storedFileName, String sha256,
                          long sizeBytes, Instant createdAt, Instant verifiedAt, String reason) {
        this.id = id;
        this.direction = direction;
        this.status = status;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.sha256 = sha256;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.verifiedAt = verifiedAt;
        this.reason = reason;
    }

    public UUID getId() { return id; }
    public TransferDirection getDirection() { return direction; }
    public TransferStatus getStatus() { return status; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStoredFileName() { return storedFileName; }
    public String getSha256() { return sha256; }
    public long getSizeBytes() { return sizeBytes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public String getReason() { return reason; }

    public TransferRecord toRecord() {
        return new TransferRecord(id, direction, status, originalFileName, storedFileName,
                sha256, sizeBytes, createdAt, verifiedAt, reason);
    }
}
