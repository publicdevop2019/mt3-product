package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatalogRepo extends JpaRepository<Catalog, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.type = ?1")
    List<Catalog> findByType(CatalogType type);
}
