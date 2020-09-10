package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.DeleteByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AppBizSkuDeleteQueryBuilder extends DeleteByIdQueryBuilder<BizSku> {

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
