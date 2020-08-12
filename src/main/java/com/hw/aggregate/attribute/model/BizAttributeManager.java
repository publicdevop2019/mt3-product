package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizAttributeManager extends RestfulEntityManager<BizAttribute> {
    @Autowired
    private AdminAttributeDeleteQueryBuilder adminAttributeDeleteQueryBuilder;

    @Autowired
    private AdminAttributeSelectQueryBuilder adminAttributeSelectQueryBuilder;

    @Autowired
    private AppAttributeSelectQueryBuilder appAttributeSelectQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.APP, appAttributeSelectQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminAttributeSelectQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminAttributeDeleteQueryBuilder);
    }
}
