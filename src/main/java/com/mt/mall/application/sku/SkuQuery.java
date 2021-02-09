package com.mt.mall.application.sku;

public class SkuQuery {
    private final String value;

    public SkuQuery(String queryParam) {
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
