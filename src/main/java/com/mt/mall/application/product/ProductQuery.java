package com.mt.mall.application.product;

import com.mt.common.query.DefaultQuery;

public class ProductQuery extends DefaultQuery {
    private final String value;

    public ProductQuery(String queryParam) {
        super(queryParam);
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
