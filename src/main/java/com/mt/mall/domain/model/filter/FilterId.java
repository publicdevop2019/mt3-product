package com.mt.mall.domain.model.filter;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class FilterId extends DomainId implements Serializable {
    private static final long serialVersionUID = 1L;
    public FilterId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("3F" + s.toUpperCase());
    }

    public FilterId(String domainId) {
        super(domainId);
    }
}
