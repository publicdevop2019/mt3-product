package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.BizCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BizCatalogRepository extends JpaRepository<BizCatalog, Long> {
}
