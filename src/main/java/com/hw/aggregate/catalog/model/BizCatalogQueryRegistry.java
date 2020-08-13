package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizCatalogQueryRegistry extends RestfulEntityManager<BizCatalog> {
    @Autowired
    private PublicBizCatalogSelectQueryBuilder publicCatalogSelectQueryBuilder;

    @Autowired
    private AdminBizCatalogDeleteQueryBuilder adminCatalogDeleteQueryBuilder;

    @Autowired
    private AdminBizCatalogSelectQueryBuilder adminCatalogSelectQueryBuilder;
    @Autowired
    private AdminBizCatalogUpdateQueryBuilder adminCatalogUpdateQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RestfulEntityManager.RoleEnum.PUBLIC, publicCatalogSelectQueryBuilder);
        this.selectQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminCatalogSelectQueryBuilder);
        this.deleteQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminCatalogDeleteQueryBuilder);
        this.updateQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminCatalogUpdateQueryBuilder);
    }
}
