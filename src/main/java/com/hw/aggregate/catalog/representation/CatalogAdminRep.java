package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.Set;

@Data
public class CatalogAdminRep {
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private Catalog.CatalogType catalogType;

    public CatalogAdminRep(Catalog catalog) {
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
    }
}
