package com.hw.aggregate.tag.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.tag.model.BizTag.NAME_LITERAL;
import static com.hw.aggregate.tag.model.BizTag.TYPE_LITERAL;


@Component
public class AdminBizTagSelectQueryBuilder extends SelectQueryBuilder<BizTag> {

    AdminBizTagSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 1000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(TYPE_LITERAL, TYPE_LITERAL);
        supportedWhereField.put(NAME_LITERAL, new SelectFieldStringLikeClause(NAME_LITERAL));
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause(TYPE_LITERAL));
        allowEmptyClause = true;
    }

}
