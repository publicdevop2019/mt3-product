package com.mt.mall.application.catalog.representation;

import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminCatalogCardRepresentation {
    private Long id;
    private String name;
    private Long parentId;
    public transient static final String ADMIN_REP_PARENT_ID_LITERAL = "parentId";
    private Set<String> attributes;
    private Type catalogType;
    public transient static final String ADMIN_REP_CATALOG_TYPE_LITERAL = "catalogType";
    private Integer version;

    public AdminCatalogCardRepresentation(Object catalog) {
        BeanUtils.copyProperties(catalog, this);
    }
}
