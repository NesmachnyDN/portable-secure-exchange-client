package io.github.nesmachnydn.secureexchange.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {
}
