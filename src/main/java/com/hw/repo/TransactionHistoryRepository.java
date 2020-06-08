package com.hw.repo;

import com.hw.entity.ChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<ChangeRecord, Long> {
    Optional<ChangeRecord> findByOptToken(String optToken);
}
