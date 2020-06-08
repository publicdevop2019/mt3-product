package com.hw.repo;

import com.hw.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionRecord, Long> {
    Optional<TransactionRecord> findByOptToken(String optToken);
}
