package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;

import java.util.Set;

@Data
public class PublicBizCatalogCardRep {
    private Long id;
    private String name;
    private Set<String> attributes;
    private Long parentId;

    public PublicBizCatalogCardRep(BizCatalog catalog) {
        this.id = catalog.getId();
        this.name = catalog.getName();
        this.attributes = catalog.getAttributes();
        this.parentId = catalog.getParentId();
    }
}
