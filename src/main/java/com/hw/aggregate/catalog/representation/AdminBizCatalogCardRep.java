package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizCatalogCardRep {
    private Long id;
    private String name;
    private Long parentId;
    public transient static final String ADMIN_REP_PARENT_ID_LITERAL = "parentId";
    private Set<String> attributes;
    private BizCatalog.CatalogType catalogType;
    public transient static final String ADMIN_REP_CATALOG_TYPE_LITERAL = "catalogType";
    private Integer version;

    public AdminBizCatalogCardRep(BizCatalog catalog) {
        BeanUtils.copyProperties(catalog, this);
    }
}
