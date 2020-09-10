package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AppBizSkuSelectQueryBuilder extends SelectQueryBuilder<BizSku> {
    AppBizSkuSelectQueryBuilder() {
        allowEmptyClause = true;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
