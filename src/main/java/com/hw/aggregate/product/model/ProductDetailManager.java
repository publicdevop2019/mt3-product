package com.hw.aggregate.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductDetailManager extends RestfulEntityManager<ProductDetail> {
    @Autowired
    private PublicSelectQueryBuilder publicSelectQueryBuilder;

    @Autowired
    private AdminSelectQueryBuilder adminSelectQueryBuilder;

    @Autowired
    private AdminProductDetailUpdateQueryBuilder adminUpdateQueryBuilder;

    @Autowired
    private AdminProductDetailDeleteQueryBuilder adminDeleteQueryBuilder;
    @Autowired
    private AppProductDetailUpdateQueryBuilder appProductDetailUpdateQueryBuilder;

    @Override
    @PostConstruct
    void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.PUBLIC, publicSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminSelectQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminUpdateQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.APP, appProductDetailUpdateQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminDeleteQueryBuilder);
    }
}
