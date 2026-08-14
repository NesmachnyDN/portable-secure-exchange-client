package io.github.nesmachnydn.secureexchange.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_event")
public class AuditEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 64)
    private String action;

    private UUID transferId;

    @Column(nullable = false, length = 1024)
    private String details;

    protected AuditEventEntity() {
    }

    public AuditEventEntity(UUID id, Instant occurredAt, String action, UUID transferId, String details) {
        this.id = id;
        this.occurredAt = occurredAt;
        this.action = action;
        this.transferId = transferId;
        this.details = details;
    }

    public UUID getId() { return id; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getAction() { return action; }
    public UUID getTransferId() { return transferId; }
    public String getDetails() { return details; }
}
