package com.hw.aggregate.product.model;

import com.hw.shared.SelectQueryBuilder;
import com.hw.shared.WhereQueryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.HashMap;

import static com.hw.aggregate.product.model.Product.*;

@Component
public class AppProductSelectQueryBuilder extends SelectQueryBuilder<Product> {

    @Autowired
    private AdminProductSelectQueryBuilder adminSelectQueryBuilder;

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    AppProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = "name";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("price", LOWEST_PRICE_LITERAL);
        mappedSortBy.put("sales", TOTAL_SALES_LITERAL);
    }

    public Predicate getWhereClause(Root<Product> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            throw new WhereQueryNotFoundException();
        Predicate queryClause = adminSelectQueryBuilder.getWhereClause(root, search);
        Predicate statusClause = getStatusClause(root);
        return cb.and(queryClause, statusClause);
    }


    private Predicate getStatusClause(Root<Product> root) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get(START_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get(START_AT_LITERAL).as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get(END_AT_LITERAL).as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get(END_AT_LITERAL).as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
