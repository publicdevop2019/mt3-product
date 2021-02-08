package com.mt.mall.port.adapter.persistence.tag;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

import static com.mt.mall.domain.model.tag.Tag.NAME_LITERAL;
import static com.mt.mall.domain.model.tag.Tag.TYPE_LITERAL;


@Component
public class AdminBizTagSelectQueryBuilder extends SelectQueryBuilder<Tag> {
    {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 1000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(TYPE_LITERAL, TYPE_LITERAL);
        supportedWhereField.put(NAME_LITERAL, new SelectFieldStringLikeClause(NAME_LITERAL));
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause(TYPE_LITERAL));
        allowEmptyClause = true;
    }
}
