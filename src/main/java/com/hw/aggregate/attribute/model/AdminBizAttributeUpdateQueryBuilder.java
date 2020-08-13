package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import static com.hw.aggregate.attribute.model.BizAttribute.METHOD_LITERAL;
import static com.hw.aggregate.attribute.model.BizAttribute.TYPE_LITERAL;
import static com.hw.aggregate.attribute.representation.AdminBizAttributeCardRep.ADMIN_REP_METHOD_LITERAL;
import static com.hw.aggregate.attribute.representation.AdminBizAttributeCardRep.ADMIN_REP_TYPE_LITERAL;

@Component
public class AdminBizAttributeUpdateQueryBuilder extends UpdateByIdQueryBuilder<BizAttribute> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @PostConstruct
    private void setUp() {
        filedMap.put(ADMIN_REP_TYPE_LITERAL, TYPE_LITERAL);
        filedMap.put(ADMIN_REP_METHOD_LITERAL, METHOD_LITERAL);
        filedTypeMap.put(ADMIN_REP_TYPE_LITERAL, this::parseType);
        filedTypeMap.put(ADMIN_REP_METHOD_LITERAL, this::parseMethod);
    }

    private BizAttribute.BizAttributeType parseType(Object o) {
        return BizAttribute.BizAttributeType.valueOf((String)o);
    }
    private BizAttribute.AttributeMethod parseMethod(Object o) {
        return BizAttribute.AttributeMethod.valueOf((String)o);
    }
}
