package com.mt.mall.domain.model.filter;

import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class FilterId extends DomainId {
    public FilterId() {
        super();
        Long id = CommonDomainRegistry.uniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3F" + s.toUpperCase());
    }

    public FilterId(String domainId) {
        super(domainId);
    }
}
