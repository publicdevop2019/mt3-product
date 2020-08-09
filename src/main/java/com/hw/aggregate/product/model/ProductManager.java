package com.hw.aggregate.product.model;

import com.hw.shared.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductManager extends RestfulEntityManager<Product> {
    @Autowired
    private PublicProductSelectQueryBuilder publicProductSelectQueryBuilder;

    @Autowired
    private AdminProductSelectQueryBuilder adminProductSelectQueryBuilder;

    @Autowired
    private AdminProductUpdateQueryBuilder adminProductUpdateQueryBuilder;

    @Autowired
    private AdminProductDeleteQueryBuilder adminProductDeleteQueryBuilder;
    @Autowired
    private AppProductUpdateQueryBuilder appProductDetailUpdateQueryBuilder;
    @Autowired
    private AppProductSelectQueryBuilder appProductSelectQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.PUBLIC, publicProductSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminProductSelectQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminProductUpdateQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminProductDeleteQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.APP, appProductSelectQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.APP, appProductDetailUpdateQueryBuilder);
    }
}
