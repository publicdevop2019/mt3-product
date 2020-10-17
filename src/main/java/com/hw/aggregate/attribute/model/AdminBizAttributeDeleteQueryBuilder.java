package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AdminBizAttributeDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizAttribute> {
}
