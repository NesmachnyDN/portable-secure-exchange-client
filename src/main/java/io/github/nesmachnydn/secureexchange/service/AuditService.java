package io.github.nesmachnydn.secureexchange.service;

import io.github.nesmachnydn.secureexchange.persistence.AuditEventEntity;
import io.github.nesmachnydn.secureexchange.persistence.AuditEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    public void record(String action, UUID transferId, String details) {
        repository.save(new AuditEventEntity(UUID.randomUUID(), Instant.now(), action, transferId, details));
    }
}
