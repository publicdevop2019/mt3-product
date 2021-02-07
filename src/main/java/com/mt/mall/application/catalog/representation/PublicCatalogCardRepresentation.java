package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Catalog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class PublicCatalogCardRepresentation {
    private Long id;
    private String name;
    private Set<String> attributes;
    private Long parentId;

    public PublicCatalogCardRepresentation(Catalog catalog) {
        BeanUtils.copyProperties(catalog, this);
    }
}
