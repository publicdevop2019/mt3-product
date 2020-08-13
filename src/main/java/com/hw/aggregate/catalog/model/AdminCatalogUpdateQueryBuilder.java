package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import static com.hw.aggregate.catalog.representation.AdminCatalogCardRep.ADMIN_REP_PARENT_ID_LITERAL;


@Component
public class AdminCatalogUpdateQueryBuilder extends UpdateByIdQueryBuilder<Catalog> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @PostConstruct
    private void setUp() {
        filedMap.put(ADMIN_REP_PARENT_ID_LITERAL, PARENT_ID_LITERAL);
        filedMap.put(ADMIN_REP_CATALOG_TYPE_LITERAL, TYPE_LITERAL);
        filedTypeMap.put(ADMIN_REP_PARENT_ID_LITERAL, this::parseType);
        filedTypeMap.put(ADMIN_REP_CATALOG_TYPE_LITERAL, this::parseMethod);
    }
}
