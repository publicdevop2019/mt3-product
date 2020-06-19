package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

@Data
public class CatalogCreatedRepresentation {
    private Long id;

    public CatalogCreatedRepresentation(Catalog catalog) {
        id = catalog.getId();
    }
}
