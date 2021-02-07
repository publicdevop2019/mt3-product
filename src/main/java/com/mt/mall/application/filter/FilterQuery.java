package com.mt.mall.application.filter;

import com.mt.mall.domain.model.catalog.Type;

import static com.mt.mall.port.adapter.persistence.catalog.CatalogSelectQueryBuilder.TYPE_LITERAL;

public class FilterQuery {
    private final String value;

    public FilterQuery(String queryParam) {
        value = queryParam;
    }

    public static FilterQuery publicQuery() {
        return new FilterQuery(TYPE_LITERAL + ":" + Type.FRONTEND.name());
    }

    public String value() {
        return value;
    }
}