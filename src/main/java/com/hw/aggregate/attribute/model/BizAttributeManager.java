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

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RestfulEntityManager.RoleEnum.PUBLIC, adminAttributeSelectQueryBuilder);
        this.deleteQueryBuilder.put(RestfulEntityManager.RoleEnum.ADMIN, adminAttributeDeleteQueryBuilder);
    }
}
