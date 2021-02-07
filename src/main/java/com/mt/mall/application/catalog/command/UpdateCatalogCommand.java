package com.mt.mall.application.catalog.command;

import com.mt.common.rest.AggregateUpdateCommand;
import com.mt.mall.domain.model.catalog.Catalog;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UpdateCatalogCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private String name;
    private String parentId;
    private Set<String> attributes;
    private Catalog.CatalogType catalogType;
    private Integer version;
}
