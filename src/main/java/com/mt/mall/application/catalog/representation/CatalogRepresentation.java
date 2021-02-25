package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

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
        setParentId(catalog.getParentId().getDomainId());
        setAttributes(catalog.getAttributes());
        setCatalogType(catalog.getType());
        setVersion(catalog.getVersion());
        this.catalogType = catalog.getType();
    }
}
