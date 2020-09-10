package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AdminBizSkuSelectQueryBuilder extends SelectQueryBuilder<BizSku> {
    AdminBizSkuSelectQueryBuilder() {
        allowEmptyClause = true;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
