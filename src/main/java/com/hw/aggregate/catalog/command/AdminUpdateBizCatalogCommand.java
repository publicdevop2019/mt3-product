package com.hw.aggregate.catalog.command;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AdminUpdateBizCatalogCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private Long parentId;
    private Set<String> attributes;
    private BizCatalog.CatalogType catalogType;
}
