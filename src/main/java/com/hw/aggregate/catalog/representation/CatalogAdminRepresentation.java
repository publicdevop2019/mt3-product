package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import lombok.Data;

import java.util.Set;

@Data
public class CatalogAdminRepresentation {
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private CatalogType catalogType;

    public CatalogAdminRepresentation(Catalog catalog) {
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
    }
}
