package com.hw.aggregate.sku.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizSkuQueryRegistry extends RestfulQueryRegistry<BizSku> {
    @Override
    public Class<BizSku> getEntityClass() {
        return BizSku.class;
    }

}
