package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.attribute.model.BizAttribute.NAME_LITERAL;
import static com.hw.aggregate.attribute.model.BizAttribute.TYPE_LITERAL;


@Component
public class AdminAttributeSelectQueryBuilder extends SelectQueryBuilder<BizAttribute> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AdminAttributeSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 200;
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("type", TYPE_LITERAL);
    }

}
