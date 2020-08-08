package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangeRecordRepository extends JpaRepository<ChangeRecord, Long> {
    Optional<ChangeRecord> findByChangeId(String changeId);
}
