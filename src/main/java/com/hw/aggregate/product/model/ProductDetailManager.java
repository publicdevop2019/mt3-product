package com.hw.aggregate.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductDetailManager extends RestfulEntityManager<ProductDetail> {
    @Autowired
    private CustomerSelectQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminSelectQueryBuilder adminQueryBuilder;

    @Autowired
    private AdminUpdateQueryBuilder adminUpdateQueryBuilder;

    @Autowired
    private AdminProductDetailDeleteQueryBuilder adminDeleteQueryBuilder;

    @Override
    @PostConstruct
    void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.CUSTOMER, customerQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminUpdateQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminDeleteQueryBuilder);
    }
}
