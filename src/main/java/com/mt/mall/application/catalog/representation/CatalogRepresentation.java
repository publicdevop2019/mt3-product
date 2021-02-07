package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class CatalogRepresentation {
    private Long id;
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private Catalog.CatalogType catalogType;

    private Integer version;

    public CatalogRepresentation(Catalog catalog) {
        BeanUtils.copyProperties(catalog, this);
        this.catalogType = catalog.getType();
    }
}
