package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizCatalogRep {
    private Long id;
    private String name;

    private Long parentId;

    private Set<String> attributes;

    private BizCatalog.CatalogType catalogType;

    private Integer version;

    public AdminBizCatalogRep(BizCatalog catalog) {
        BeanUtils.copyProperties(catalog, this);
    }
}
