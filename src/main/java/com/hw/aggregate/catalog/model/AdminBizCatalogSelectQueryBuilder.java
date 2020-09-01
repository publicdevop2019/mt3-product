package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.catalog.model.BizCatalog.*;


@Component
public class AdminBizCatalogSelectQueryBuilder extends SelectQueryBuilder<BizCatalog> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AdminBizCatalogSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        mappedSortBy.put(NAME_LITERAL, NAME_LITERAL);
        supportedWhereField.put(TYPE_LITERAL, new SelectFieldStringEqualClause<>(TYPE_LITERAL));
        supportedWhereField.put(PARENT_ID_LITERAL, new SelectFieldLongEqualClause<>(PARENT_ID_LITERAL));
        allowEmptyClause = true;
    }
}
