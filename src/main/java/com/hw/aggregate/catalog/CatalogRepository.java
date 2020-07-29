package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.type = ?1")
    Page<Catalog> findByType(CatalogType type, Pageable pageable);
}
