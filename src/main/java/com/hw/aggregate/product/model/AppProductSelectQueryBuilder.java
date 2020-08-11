package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;

import static com.hw.aggregate.product.model.Product.*;

@Component
public class AppProductSelectQueryBuilder extends SelectQueryBuilder<Product> {

    @Autowired
    private AdminProductSelectQueryBuilder adminSelectQueryBuilder;

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AppProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = "name";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("price", LOWEST_PRICE_LITERAL);
        mappedSortBy.put("sales", TOTAL_SALES_LITERAL);
        supportedWhereField.put("attr", new SelectFieldAttrLikeClause<>());
        supportedWhereField.put("name", new SelectFieldStringLikeClause<>(NAME_LITERAL));
        supportedWhereField.put("price", new SelectFieldNumberRangeClause<>(LOWEST_PRICE_LITERAL));
        defaultWhereField.add(new SelectStatusClause<>());
    }
}
