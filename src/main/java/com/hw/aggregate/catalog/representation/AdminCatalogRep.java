package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.Set;

@Data
public class AdminCatalogRep {
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private Catalog.CatalogType catalogType;

    public AdminCatalogRep(Catalog catalog) {
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
    }
}
