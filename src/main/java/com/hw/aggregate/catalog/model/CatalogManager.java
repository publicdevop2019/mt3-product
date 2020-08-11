package com.hw.aggregate.catalog.model;

import com.hw.shared.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CatalogManager extends RestfulEntityManager<Catalog> {
    @Autowired
    private PublicCatalogSelectQueryBuilder publicCatalogSelectQueryBuilder;

    @Autowired
    private AdminCatalogDeleteQueryBuilder adminCatalogDeleteQueryBuilder;

    @Autowired
    private AdminCatalogSelectQueryBuilder adminCatalogSelectQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RestfulEntityManager.RoleEnum.PUBLIC, publicCatalogSelectQueryBuilder);
        this.selectQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminCatalogSelectQueryBuilder);
        this.deleteQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminCatalogDeleteQueryBuilder);
    }
}
