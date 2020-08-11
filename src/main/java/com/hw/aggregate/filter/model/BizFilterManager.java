package com.hw.aggregate.filter.model;

import com.hw.shared.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizFilterManager extends RestfulEntityManager<BizFilter> {
    @Autowired
    private PublicFilterSelectQueryBuilder publicFilterSelectQueryBuilder;

    @Autowired
    private AdminFilterDeleteQueryBuilder adminFilterDeleteQueryBuilder;

    @Autowired
    private AdminFilterSelectQueryBuilder adminFilterSelectQueryBuilder;

    @PostConstruct
    @Override
    protected void configQueryBuilder() {
        this.selectQueryBuilder.put(RoleEnum.PUBLIC, publicFilterSelectQueryBuilder);
        this.deleteQueryBuilder.put(RoleEnum.ADMIN, adminFilterDeleteQueryBuilder);
        this.selectQueryBuilder.put(RoleEnum.ADMIN, adminFilterSelectQueryBuilder);
    }
}
