package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogCardRepresentation {
    public transient static final String ADMIN_REP_PARENT_ID_LITERAL = "parentId";
    public transient static final String ADMIN_REP_CATALOG_TYPE_LITERAL = "catalogType";
    private String id;
    private String name;
    private String parentId;
    private Set<String> attributes;
    private Type catalogType;
    private Integer version;
    private boolean reviewRequired = false;

    public CatalogCardRepresentation(Catalog catalog, boolean reviewRequired) {
        setId(catalog.getCatalogId().getDomainId());
        setName(catalog.getName());
        if (catalog.getParentId() != null)
            setParentId(catalog.getParentId().getDomainId());
        setAttributes(catalog.getLinkedTags().stream().map(e -> String.join(":", e.getTagId().getDomainId(), e.getTagValue())).collect(Collectors.toSet()));
        setCatalogType(catalog.getType());
        setVersion(catalog.getVersion());
        setReviewRequired(reviewRequired);
    }
}
