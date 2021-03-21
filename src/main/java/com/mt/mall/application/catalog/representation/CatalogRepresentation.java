package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogRepresentation {
    private String id;
    private String name;

    private String parentId;

    private Set<String> attributes;

    private Type catalogType;

    private Integer version;

    public CatalogRepresentation(Catalog catalog) {
        setId(catalog.getCatalogId().getDomainId());
        setName(catalog.getName());
        if (catalog.getParentId() != null)
            setParentId(catalog.getParentId().getDomainId());
        setAttributes(catalog.getLinkedTags().stream().map(e -> String.join(":", e.getTagId().getDomainId(), e.getTagValue())).collect(Collectors.toSet()));
        setCatalogType(catalog.getType());
        setVersion(catalog.getVersion());
        this.catalogType = catalog.getType();
    }
}
