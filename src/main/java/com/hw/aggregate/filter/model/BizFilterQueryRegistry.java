package com.hw.aggregate.filter.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizFilterQueryRegistry extends RestfulQueryRegistry<BizFilter> {
    @Override
    public Class<BizFilter> getEntityClass() {
        return BizFilter.class;
    }
}
