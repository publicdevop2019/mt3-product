package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizFilterRepository extends JpaRepository<BizFilter, Long> {
}
