package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PublicCatalogCardRepresentation {
    private String id;
    private String name;
    private Set<String> attributes;
    private String parentId;

    public PublicCatalogCardRepresentation(Object o) {
        Catalog o1 = (Catalog) o;
        id = o1.getCatalogId().getDomainId();
        name = o1.getName();
        setAttributes(o1.getLinkedTags().stream().map(e -> String.join(":", e.getTagId().getDomainId(), e.getTagValue())).collect(Collectors.toSet()));
        if (o1.getParentId() != null)
            parentId = o1.getParentId().getDomainId();
    }
}
