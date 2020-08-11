package com.hw.aggregate.product.model;

import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductSkuManager extends RestfulEntityManager<ProductSku> {

    @Autowired
    private AdminProductSkuDeleteQueryBuilder adminDeleteQueryBuilder;

    @Autowired
    private AdminProductSkuUpdateQueryBuilder adminProductSkuUpdateQueryBuilder;
    @Autowired
    private AppProductSkuUpdateQueryBuilder appProductSkuUpdateQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminDeleteQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminProductSkuUpdateQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.APP, appProductSkuUpdateQueryBuilder);
    }
}
