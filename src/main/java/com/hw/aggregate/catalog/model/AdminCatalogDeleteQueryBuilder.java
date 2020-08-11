package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.DeleteByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AdminCatalogDeleteQueryBuilder extends DeleteByIdQueryBuilder<Catalog> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
