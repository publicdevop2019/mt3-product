package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.BizCatalog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class PublicBizCatalogCardRep {
    private Long id;
    private String name;
    private Set<String> attributes;
    private Long parentId;

    public PublicBizCatalogCardRep(BizCatalog catalog) {
        BeanUtils.copyProperties(catalog, this);
    }
}
