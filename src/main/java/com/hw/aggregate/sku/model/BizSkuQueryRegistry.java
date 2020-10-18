package com.hw.aggregate.sku.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizSkuQueryRegistry extends RestfulQueryRegistry<BizSku> {

    @Autowired
    private AdminBizSkuDeleteQueryBuilder adminDeleteQueryBuilder;

    @Autowired
    private AdminBizSkuUpdateQueryBuilder adminProductSkuUpdateQueryBuilder;
    @Autowired
    private AppBizSkuUpdateQueryBuilder appProductSkuUpdateQueryBuilder;
    @Autowired
    private AppBizSkuSelectQueryBuilder appBizSkuSelectQueryBuilder;
    @Autowired
    private AdminBizSkuSelectQueryBuilder adminBizSkuSelectQueryBuilder;
    @Autowired
    private AppBizSkuDeleteQueryBuilder appBizSkuDeleteQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminDeleteQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.APP, appBizSkuDeleteQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminProductSkuUpdateQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminBizSkuSelectQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.APP, appProductSkuUpdateQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.APP, appBizSkuSelectQueryBuilder);
    }
}
