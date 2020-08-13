package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_NAME_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SALES_LITERAL;
import static com.hw.aggregate.product.representation.PublicProductCardRep.PUBLIC_REP_PRICE_LITERAL;
import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class PublicProductSelectQueryBuilder extends SelectQueryBuilder<Product> {

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    PublicProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = ADMIN_REP_NAME_LITERAL;
        mappedSortBy.put(ADMIN_REP_NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(ADMIN_REP_SALES_LITERAL, TOTAL_SALES_LITERAL);
        mappedSortBy.put(PUBLIC_REP_PRICE_LITERAL, LOWEST_PRICE_LITERAL);
        supportedWhereField.put("attr", new SelectFieldAttrLikeClause<>());
        supportedWhereField.put("name", new SelectFieldStringLikeClause<>(NAME_LITERAL));
        supportedWhereField.put("price", new SelectFieldNumberRangeClause<>(LOWEST_PRICE_LITERAL));
        defaultWhereField.add(new SelectStatusClause<>());

        mappedSortBy.remove(COMMON_ENTITY_ID);
    }

}
