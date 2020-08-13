package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.Set;

@Data
public class PublicCatalogCardRep {
    private Long id;
    private String name;
    private Set<String> attributes;
    private Long parentId;

    public PublicCatalogCardRep(Catalog catalog) {
        this.id = catalog.getId();
        this.name = catalog.getName();
        this.attributes = catalog.getAttributes();
        this.parentId = catalog.getParentId();
    }
}
