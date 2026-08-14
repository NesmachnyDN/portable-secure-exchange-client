package io.github.nesmachnydn.secureexchange.domain;

import java.time.Instant;
import java.util.UUID;

public record TransferRecord(
        UUID id,
        TransferDirection direction,
        TransferStatus status,
        String originalFileName,
        String storedFileName,
        String sha256,
        long sizeBytes,
        Instant createdAt,
        Instant verifiedAt,
        String reason) {
}
