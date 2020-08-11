package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.AdminProductRep.*;
import static com.hw.aggregate.product.representation.PublicProductSumPagedRep.ProductCardRepresentation.PUBLIC_REP_PRICE_LITERAL;
import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;


@Component
public class AdminProductSelectQueryBuilder extends SelectQueryBuilder<Product> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }


    AdminProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        DEFAULT_SORT_BY = COMMON_ENTITY_ID;
        mappedSortBy = new HashMap<>();
        mappedSortBy.put(COMMON_ENTITY_ID, COMMON_ENTITY_ID);
        mappedSortBy.put(ADMIN_REP_NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(ADMIN_REP_SALES_LITERAL, TOTAL_SALES_LITERAL);
        mappedSortBy.put(PUBLIC_REP_PRICE_LITERAL, LOWEST_PRICE_LITERAL);
        mappedSortBy.put(ADMIN_REP_END_AT_LITERAL, END_AT_LITERAL);
        supportedWhereField.put("attr", new SelectFieldAttrLikeClause<>());
        supportedWhereField.put("name", new SelectFieldStringLikeClause<>(NAME_LITERAL));
        supportedWhereField.put("price", new SelectFieldNumberRangeClause<>(LOWEST_PRICE_LITERAL));
    }

}
