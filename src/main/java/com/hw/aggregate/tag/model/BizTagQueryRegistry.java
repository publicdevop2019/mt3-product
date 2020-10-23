package com.hw.aggregate.tag.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizTagQueryRegistry extends RestfulQueryRegistry<BizTag> {

    @Override
    public Class<BizTag> getEntityClass() {
        return BizTag.class;
    }
}
