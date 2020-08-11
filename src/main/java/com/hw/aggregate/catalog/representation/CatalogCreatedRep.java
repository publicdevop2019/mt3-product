package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

@Data
public class CatalogCreatedRep {
    private Long id;

    public CatalogCreatedRep(Catalog catalog) {
        id = catalog.getId();
    }
}
