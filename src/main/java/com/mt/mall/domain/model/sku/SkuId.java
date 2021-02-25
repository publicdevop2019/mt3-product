package com.mt.mall.domain.model.sku;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class SkuId extends DomainId {
    public SkuId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3S" + s.toUpperCase());
    }

    public SkuId(String domainId) {
        super(domainId);
    }
}
