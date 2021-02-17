package com.mt.mall.application.catalog;

import com.mt.common.query.DefaultQuery;
import com.mt.mall.domain.model.catalog.Type;

import static com.mt.mall.port.adapter.persistence.catalog.CatalogSelectQueryBuilder.TYPE_LITERAL;


public class CatalogQuery extends DefaultQuery {
    private final String value;

    public CatalogQuery(String queryParam) {
        super(queryParam);
        value = queryParam;
    }

    public static CatalogQuery publicQuery() {
        return new CatalogQuery(TYPE_LITERAL + ":" + Type.FRONTEND.name());
    }

    public String value() {
        return value;
    }
}
