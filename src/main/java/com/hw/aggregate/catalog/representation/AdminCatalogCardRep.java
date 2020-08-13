package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.Set;

@Data
public class AdminCatalogCardRep {
    private Long id;
    private String name;
    private Long parentId;
    public transient static final String ADMIN_REP_PARENT_ID_LITERAL = "parentId";
    private Set<String> attributes;
    private Catalog.CatalogType catalogType;
    public transient static final String ADMIN_REP_CATALOG_TYPE_LITERAL = "catalogType";

    public AdminCatalogCardRep(Catalog catalog) {
        this.id = catalog.getId();
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
    }
}
