package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BizFilterRepository extends JpaRepository<BizFilter, Long> {
}
