package com.hw.aggregate.filter.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.filter.model.BizFilter.LINKED_CATALOG_LITERAL;

@Component
public class AdminFilterSelectQueryBuilder extends SelectQueryBuilder<BizFilter> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AdminFilterSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        supportedWhereField.put("catalog", new SelectFieldStringEqualClause<>(LINKED_CATALOG_LITERAL));
    }

}
