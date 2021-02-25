package com.mt.mall.domain.model.tag;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class TagId extends DomainId {
    public TagId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3T" + s.toUpperCase());
    }

    public TagId(String domainId) {
        super(domainId);
    }
}
