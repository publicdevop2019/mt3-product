package com.mt.mall.application.catalog;

import com.mt.mall.domain.model.catalog.Type;

import static com.mt.mall.domain.model.catalog.Catalog.TYPE_LITERAL;

public class CatalogQuery {
    private final String value;

    public CatalogQuery(String queryParam) {
        value = queryParam;
    }

    public static CatalogQuery publicQuery() {
        return new CatalogQuery(TYPE_LITERAL + ":" + Type.FRONTEND.name());
    }

    public String value() {
        return value;
    }
}
