package com.hw.aggregate.product;

import com.hw.aggregate.product.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionRecord, Long> {
    Optional<TransactionRecord> findByTransactionId(String optToken);
}
