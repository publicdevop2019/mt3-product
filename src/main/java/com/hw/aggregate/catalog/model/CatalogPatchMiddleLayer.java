package com.hw.aggregate.catalog.model;

import lombok.Data;

import java.util.Set;

@Data
public class CatalogPatchMiddleLayer {

    private Long id;

    private String name;

    private Long parentId;

    private Set<String> attributes;

    private Catalog.CatalogType type;

}
