package com.mt.mall.domain.model.catalog;

import com.mt.common.CommonConstant;
import com.mt.common.domain.model.restful.query.QueryCriteria;

import java.util.Set;

import static com.mt.mall.port.adapter.persistence.catalog.CatalogQueryBuilder.TYPE_LITERAL;

public class CatalogQuery extends QueryCriteria {

    public CatalogQuery(String query) {
        super(query);
    }

    public CatalogQuery(CatalogId catalogId) {
        super(catalogId);
    }

    public CatalogQuery(Set<String> domainIds) {
        super(domainIds);
    }

    public static CatalogQuery publicQuery() {
        return new CatalogQuery(TYPE_LITERAL + CommonConstant.QUERY_DELIMITER + Type.FRONTEND.name());
    }
}
