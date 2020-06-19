package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

@Data
public class CatalogRepresentation {
    private Long id;
    private String title;

    public CatalogRepresentation(Catalog catalog) {
        id = catalog.getId();
        title = catalog.getName();
    }
}
