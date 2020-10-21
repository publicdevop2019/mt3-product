package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.AdminProductRep.*;


@Component
public class AdminProductSelectQueryBuilder extends SelectQueryBuilder<Product> {


    AdminProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        mappedSortBy.put(ADMIN_REP_NAME_LITERAL, PRODUCT_NAME_LITERAL);
        mappedSortBy.put(ADMIN_REP_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL);
        mappedSortBy.put(ADMIN_REP_PRICE_LITERAL, PRODUCT_LOWEST_PRICE_LITERAL);
        mappedSortBy.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
        supportedWhereField.put("attributes", new SelectProductAttrClause());
        supportedWhereField.put(ADMIN_REP_NAME_LITERAL, new SelectFieldStringLikeClause(PRODUCT_NAME_LITERAL));
        supportedWhereField.put(ADMIN_REP_PRICE_LITERAL, new SelectFieldNumberRangeClause(PRODUCT_LOWEST_PRICE_LITERAL));
        allowEmptyClause = true;
    }

}
