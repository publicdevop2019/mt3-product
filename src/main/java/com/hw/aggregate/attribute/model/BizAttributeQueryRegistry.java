package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizAttributeQueryRegistry extends RestfulQueryRegistry<BizAttribute> {

    @Override
    public Class<BizAttribute> getEntityClass() {
        return BizAttribute.class;
    }
}
