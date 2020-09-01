package com.hw.aggregate.filter.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldCollectionContainsClause;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.filter.model.BizFilter.ENTITY_CATALOG_LITERAL;

@Component
public class AdminBizFilterSelectQueryBuilder extends SelectQueryBuilder<BizFilter> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AdminBizFilterSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        supportedWhereField.put(ENTITY_CATALOG_LITERAL, new SelectFieldCollectionContainsClause<>(ENTITY_CATALOG_LITERAL));
        allowEmptyClause=true;
    }

}
