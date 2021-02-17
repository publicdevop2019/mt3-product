package com.mt.mall.application.sku;

import com.mt.common.query.DefaultQuery;

public class SkuQuery extends DefaultQuery {
    private final String value;

    public SkuQuery(String queryParam) {
        super(queryParam);
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
