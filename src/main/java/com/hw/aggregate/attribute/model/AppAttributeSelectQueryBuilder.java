package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AppAttributeSelectQueryBuilder extends SelectQueryBuilder<BizAttribute> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AppAttributeSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 20;
    }
}
