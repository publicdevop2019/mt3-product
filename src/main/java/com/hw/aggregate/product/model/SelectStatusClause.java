package com.hw.aggregate.product.model;

import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;

import static com.hw.aggregate.product.model.Product.PRODUCT_END_AT_LITERAL;
import static com.hw.aggregate.product.model.Product.PRODUCT_START_AT_LITERAL;

public class SelectStatusClause<T> extends WhereClause<T> {
    @Override
    public Predicate getWhereClause(String str, CriteriaBuilder cb, Root<T> root, Object query) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get(PRODUCT_START_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get(PRODUCT_START_AT_LITERAL).as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get(PRODUCT_END_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get(PRODUCT_END_AT_LITERAL).as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
