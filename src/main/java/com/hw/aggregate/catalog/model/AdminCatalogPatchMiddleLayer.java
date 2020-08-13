package com.hw.aggregate.catalog.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class AdminCatalogPatchMiddleLayer extends TypedClass<AdminCatalogPatchMiddleLayer> {

    private String name;

    private Long parentId;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributes;

    private Catalog.CatalogType type;

    public AdminCatalogPatchMiddleLayer(Catalog catalog) {
        super(AdminCatalogPatchMiddleLayer.class);
        this.name = catalog.getName();
        this.parentId = catalog.getParentId();
        this.attributes = catalog.getAttributes();
        this.type = catalog.getType();
    }

    public AdminCatalogPatchMiddleLayer() {
        super(AdminCatalogPatchMiddleLayer.class);
    }
}
