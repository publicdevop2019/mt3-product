package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.PublicProductCardRep.*;
import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class PublicProductSelectQueryBuilder extends SelectQueryBuilder<Product> {

    public static final String PUBLIC_ATTR = "attr";

    PublicProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = PUBLIC_REP_NAME_LITERAL;
        mappedSortBy.put(PUBLIC_REP_NAME_LITERAL, PRODUCT_NAME_LITERAL);
        mappedSortBy.put(PUBLIC_REP_TOTAL_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL);
        mappedSortBy.put(PUBLIC_REP_PRICE_LITERAL, PRODUCT_LOWEST_PRICE_LITERAL);
        supportedWhereField.put(PUBLIC_ATTR, new SelectProductAttrClause());
        supportedWhereField.put(PUBLIC_REP_NAME_LITERAL, new SelectFieldStringLikeClause(PRODUCT_NAME_LITERAL));
        supportedWhereField.put(PUBLIC_REP_PRICE_LITERAL, new SelectFieldNumberRangeClause(PRODUCT_LOWEST_PRICE_LITERAL));
        defaultWhereField.add(new SelectStatusClause());
        mappedSortBy.remove(COMMON_ENTITY_ID);
    }

}
