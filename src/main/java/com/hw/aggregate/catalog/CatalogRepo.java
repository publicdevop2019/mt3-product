package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepo extends JpaRepository<Catalog, Long> {
}
