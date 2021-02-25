package com.mt.mall.domain.model.product;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class ProductId extends DomainId {
    public ProductId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3P" + s.toUpperCase());
    }

    public ProductId(String domainId) {
        super(domainId);
    }
}
