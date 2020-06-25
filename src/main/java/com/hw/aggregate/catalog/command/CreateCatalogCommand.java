package com.hw.aggregate.catalog.command;

import com.hw.aggregate.catalog.model.CatalogType;
import lombok.Data;

import java.util.Set;

@Data
public class CreateCatalogCommand {
    private String name;
    private Long parentId;
    private Set<String> attributesSearch;
    private CatalogType catalogType;
}
