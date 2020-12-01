package com.hw.aggregate.filter.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.filter.model.BizFilter.ENTITY_CATALOG_LITERAL;

@Component
public class AdminBizFilterSelectQueryBuilder extends SelectQueryBuilder<BizFilter> {
    {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        supportedWhereField.put(ENTITY_CATALOG_LITERAL, new SelectFieldStringLikeClause(ENTITY_CATALOG_LITERAL));
        allowEmptyClause = true;
    }

}
