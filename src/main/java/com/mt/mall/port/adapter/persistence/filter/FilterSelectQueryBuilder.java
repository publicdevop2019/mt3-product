package com.mt.mall.port.adapter.persistence.filter;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.common.sql.clause.SelectFieldStringLikeClause;
import com.mt.mall.domain.model.filter.Filter;
import org.springframework.stereotype.Component;
@Component
public class FilterSelectQueryBuilder extends SelectQueryBuilder<Filter> {
    public transient static final String ENTITY_CATALOG_LITERAL = "catalogs";
    {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        supportedWhereField.put(ENTITY_CATALOG_LITERAL, new SelectFieldStringLikeClause(ENTITY_CATALOG_LITERAL));
        supportedWhereField.put("catalog", new SelectFieldStringEqualClause<>(ENTITY_CATALOG_LITERAL));
        allowEmptyClause = true;
    }

}
