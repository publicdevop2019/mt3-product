package com.hw.aggregate.product.model;

import com.hw.shared.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductManager extends RestfulEntityManager<Product> {
    @Autowired
    private PublicProductSelectQueryBuilder publicSelectQueryBuilder;

    @Autowired
    private AdminProductSelectQueryBuilder adminSelectQueryBuilder;

    @Autowired
    private AdminProductUpdateQueryBuilder adminUpdateQueryBuilder;

    @Autowired
    private AdminProductDeleteQueryBuilder adminDeleteQueryBuilder;
    @Autowired
    private AppProductUpdateQueryBuilder appProductDetailUpdateQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.PUBLIC, publicSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminSelectQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminUpdateQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.APP, appProductDetailUpdateQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminDeleteQueryBuilder);
    }
}
