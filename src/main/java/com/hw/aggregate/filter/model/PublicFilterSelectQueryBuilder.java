package com.hw.aggregate.filter.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.filter.model.BizFilter.LINKED_CATALOG_LITERAL;

@Component
public class PublicFilterSelectQueryBuilder extends SelectQueryBuilder<BizFilter> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    PublicFilterSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 5;
        supportedWhereField.put("catalog", new SelectFieldStringEqualClause<>(LINKED_CATALOG_LITERAL));
    }
}
