package com.mt.mall.application.catalog.command;

import com.mt.mall.domain.model.catalog.Catalog;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class CreateCatalogCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String parentId;
    private Set<String> attributes;
    private Catalog.CatalogType catalogType;
}
