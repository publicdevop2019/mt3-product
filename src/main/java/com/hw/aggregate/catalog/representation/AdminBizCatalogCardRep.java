package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizCatalogCardRep {
    private Long id;
    private String name;
    private Long parentId;
    public transient static final String ADMIN_REP_PARENT_ID_LITERAL = "parentId";
    private Set<String> attributes;
    private BizCatalog.CatalogType catalogType;
    public transient static final String ADMIN_REP_CATALOG_TYPE_LITERAL = "catalogType";
    private Integer version;

    public AdminBizCatalogCardRep(BizCatalog catalog) {
        this.id = catalog.getId();
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.catalogType = catalog.getType();
        this.version = catalog.getVersion();
    }
}
