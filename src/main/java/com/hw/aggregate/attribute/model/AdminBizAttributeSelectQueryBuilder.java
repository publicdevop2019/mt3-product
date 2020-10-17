package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.attribute.model.BizAttribute.NAME_LITERAL;
import static com.hw.aggregate.attribute.model.BizAttribute.TYPE_LITERAL;


@Component
public class AdminBizAttributeSelectQueryBuilder extends SelectQueryBuilder<BizAttribute> {

    AdminBizAttributeSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 1000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(TYPE_LITERAL, TYPE_LITERAL);
        supportedWhereField.put(NAME_LITERAL, new SelectFieldStringLikeClause<>(NAME_LITERAL));
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause<>(TYPE_LITERAL));
        allowEmptyClause = true;
    }

}
