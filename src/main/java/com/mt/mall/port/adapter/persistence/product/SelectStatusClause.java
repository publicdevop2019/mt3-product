package com.mt.mall.port.adapter.persistence.product;

import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.*;
import java.time.Instant;

import static com.mt.mall.domain.model.product.Product.PRODUCT_END_AT_LITERAL;
import static com.mt.mall.domain.model.product.Product.PRODUCT_START_AT_LITERAL;

public class SelectStatusClause<T> extends WhereClause<T> {
    @Override
    public Predicate getWhereClause(String str, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> query) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get(PRODUCT_START_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get(PRODUCT_START_AT_LITERAL).as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get(PRODUCT_END_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get(PRODUCT_END_AT_LITERAL).as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
