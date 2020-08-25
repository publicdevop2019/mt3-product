package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizAttributeQueryRegistry extends RestfulQueryRegistry<BizAttribute> {
    @Autowired
    private AdminBizAttributeDeleteQueryBuilder adminAttributeDeleteQueryBuilder;

    @Autowired
    private AdminBizAttributeSelectQueryBuilder adminAttributeSelectQueryBuilder;

    @Autowired
    private AppBizAttributeSelectQueryBuilder appAttributeSelectQueryBuilder;

    @Autowired
    private AdminBizAttributeUpdateQueryBuilder adminBizAttributeUpdateQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.APP, appAttributeSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminAttributeSelectQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminAttributeDeleteQueryBuilder);
        this.updateQueryBuilder.put(RoleEnum.ADMIN, adminBizAttributeUpdateQueryBuilder);
    }
}
