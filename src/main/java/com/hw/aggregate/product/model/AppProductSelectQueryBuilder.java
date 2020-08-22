package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

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
        defaultWhereField.add(new SelectStatusClause<>());
    }
}
