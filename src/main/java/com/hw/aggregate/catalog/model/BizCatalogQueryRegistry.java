package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizCatalogQueryRegistry extends RestfulQueryRegistry<BizCatalog> {
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
        this.selectQueryBuilder.put(RoleEnum.PUBLIC, publicCatalogSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminCatalogSelectQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminCatalogDeleteQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminCatalogUpdateQueryBuilder);
    }
}
