package com.hw.aggregate.product.model;

import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;

import static com.hw.aggregate.product.model.Product.END_AT_LITERAL;
import static com.hw.aggregate.product.model.Product.START_AT_LITERAL;

public class SelectStatusClause<T> extends WhereClause<T> {
    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get(START_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get(START_AT_LITERAL).as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get(END_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get(END_AT_LITERAL).as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
