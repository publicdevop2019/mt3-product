package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class CatalogId extends DomainId implements Serializable {
    public CatalogId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3C" + s.toUpperCase());
    }

    public CatalogId(String domainId) {
        super(domainId);
    }
}
