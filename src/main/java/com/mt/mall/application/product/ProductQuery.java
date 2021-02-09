package com.mt.mall.application.product;

public class ProductQuery {
    private final String value;

    public ProductQuery(String queryParam) {
        value = queryParam;
    }

    public String value() {
        return value;
    }
}
