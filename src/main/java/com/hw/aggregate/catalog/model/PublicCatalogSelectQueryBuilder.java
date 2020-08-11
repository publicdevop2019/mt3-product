package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Component
public class PublicCatalogSelectQueryBuilder extends SelectQueryBuilder<Catalog> {
    @Autowired
    private AdminCatalogSelectQueryBuilder adminQueryBuilder;

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    public Predicate getWhereClause(Root<Catalog> root, String search) {
        return adminQueryBuilder.getWhereClause(root, "type:" + Catalog.CatalogType.FRONTEND.name());
    }

    PublicCatalogSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 1500;
    }

}
