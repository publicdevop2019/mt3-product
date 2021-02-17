package com.mt.mall.port.adapter.persistence.filter;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.DomainIdQueryClause;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.common.sql.clause.SelectFieldStringLikeClause;
import com.mt.mall.domain.model.filter.Filter;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;

@Component
public class FilterSelectQueryBuilder extends SelectQueryBuilder<Filter> {
    public transient static final String ENTITY_CATALOG_LITERAL = "catalogs";
    private static final String FILTER_ID_LITERAL = "filterId";

    {
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(FILTER_ID_LITERAL));
        supportedWhere.put(ENTITY_CATALOG_LITERAL, new SelectFieldStringLikeClause(ENTITY_CATALOG_LITERAL));
        supportedWhere.put("catalog", new SelectFieldStringEqualClause<>(ENTITY_CATALOG_LITERAL));
    }

}
