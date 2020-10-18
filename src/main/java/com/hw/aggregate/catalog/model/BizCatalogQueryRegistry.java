package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizCatalogQueryRegistry extends RestfulQueryRegistry<BizCatalog> {

    @Override
    public Class<BizCatalog> getEntityClass() {
        return BizCatalog.class;
    }
}
