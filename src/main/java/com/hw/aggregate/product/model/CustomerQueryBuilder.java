package com.hw.aggregate.product.model;

import com.hw.shared.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.HashMap;

@Component("productCustomer")
public class CustomerQueryBuilder extends QueryBuilder<ProductDetail> {
    @Autowired
    private AdminQueryBuilder config;

    CustomerQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = "name";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("name", "name");
        mappedSortBy.put("price", "lowestPrice");
        mappedSortBy.put("sales", "totalSales");
    }

    @Override
    public Predicate getQueryClause(CriteriaBuilder cb, Root<ProductDetail> root, String search) {
        if (search == null)
            return null;
        Predicate queryClause = config.getQueryClause(cb, root, search);
        Predicate statusClause = getStatusClause(cb, root);
        return cb.and(queryClause, statusClause);
    }

    private Predicate getStatusClause(CriteriaBuilder cb, Root<ProductDetail> root) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get("startAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get("startAt").as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get("endAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get("endAt").as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
