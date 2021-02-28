package com.mt.mall.port.adapter.persistence.product;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldNumberRangeClause;
import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;
import com.mt.mall.domain.model.product.Product;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.mall.application.product.representation.ProductRepresentation.*;
import static com.mt.mall.domain.model.product.Product.*;
import static com.mt.mall.domain.model.product.ProductQuery.AVAILABLE;


@Component
public class ProductQueryBuilder extends SelectQueryBuilder<Product> {
    public static final String PUBLIC_ATTR = "attr";
    private static final String PRODUCT_ID_LITERAL = "productId";

    {
        supportedSort.put("id", PRODUCT_ID_LITERAL);
        supportedSort.put(ADMIN_REP_NAME_LITERAL, PRODUCT_NAME_LITERAL);
        supportedSort.put(ADMIN_REP_SALES_LITERAL, PRODUCT_TOTAL_SALES_LITERAL);
        supportedSort.put(ADMIN_REP_PRICE_LITERAL, PRODUCT_LOWEST_PRICE_LITERAL);
        supportedSort.put(ADMIN_REP_END_AT_LITERAL, PRODUCT_END_AT_LITERAL);
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(PRODUCT_ID_LITERAL));
        supportedWhere.put(PUBLIC_ATTR, new SelectProductAttrClause<>());
        supportedWhere.put("attributes", new SelectProductAttrClause<>());
        supportedWhere.put(ADMIN_REP_NAME_LITERAL, new FieldStringLikeClause<>(PRODUCT_NAME_LITERAL));
        supportedWhere.put(ADMIN_REP_PRICE_LITERAL, new FieldNumberRangeClause<>(PRODUCT_LOWEST_PRICE_LITERAL));
        supportedWhere.put(AVAILABLE, new SelectStatusClause<>());
    }
}
