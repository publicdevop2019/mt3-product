package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.DomainIdQueryClause;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.common.sql.clause.SelectFieldStringLikeClause;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;


@Component
public class TagSelectQueryBuilder extends SelectQueryBuilder<Tag> {
    public transient static final String NAME_LITERAL = "name";
    public transient static final String TAG_ID_LITERAL = "tagId";
    public transient static final String TYPE_LITERAL = "type";

    {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 1000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(TYPE_LITERAL, TYPE_LITERAL);
        supportedWhereField.put(NAME_LITERAL, new SelectFieldStringLikeClause(NAME_LITERAL));
        supportedWhereField.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(TAG_ID_LITERAL));
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause(TYPE_LITERAL));
        allowEmptyClause = true;
    }
}
