package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;


@Component
public class TagQueryBuilder extends SelectQueryBuilder<Tag> {
    public transient static final String NAME_LITERAL = "name";
    public transient static final String TAG_ID_LITERAL = "tagId";
    public transient static final String TYPE_LITERAL = "type";

    {
        supportedSort.put("id", TAG_ID_LITERAL);
        supportedSort.put(NAME_LITERAL, NAME_LITERAL);
        supportedSort.put(TYPE_LITERAL, TYPE_LITERAL);
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(TAG_ID_LITERAL));
        supportedWhere.put(NAME_LITERAL, new FieldStringLikeClause<>(NAME_LITERAL));
        supportedWhere.put(TYPE_LITERAL, new FieldStringEqualClause<>(TYPE_LITERAL));
    }
}
