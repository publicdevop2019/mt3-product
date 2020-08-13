package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizCatalogRep {
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private BizCatalog.CatalogType catalogType;

    public AdminBizCatalogRep(BizCatalog catalog) {
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
    }
}
