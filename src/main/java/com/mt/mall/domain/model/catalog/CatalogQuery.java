package com.mt.mall.domain.model.catalog;

import com.mt.common.query.QueryCriteria;

import static com.mt.mall.port.adapter.persistence.catalog.CatalogQueryBuilder.TYPE_LITERAL;

public class CatalogQuery extends QueryCriteria {

    public CatalogQuery(String query) {
        super(query);
    }

    public CatalogQuery(CatalogId catalogId) {
        super(catalogId);
    }

    public static CatalogQuery publicQuery() {
        return new CatalogQuery(TYPE_LITERAL + ":" + Type.FRONTEND.name());
    }
}