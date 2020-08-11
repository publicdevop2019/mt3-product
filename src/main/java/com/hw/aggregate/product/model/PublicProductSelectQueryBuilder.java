package com.hw.aggregate.product.model;

import com.hw.shared.WhereQueryNotFoundException;
import com.hw.shared.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.HashMap;

@Component("productPublic")
public class PublicProductSelectQueryBuilder extends SelectQueryBuilder<Product> {

    @Autowired
    private AdminProductSelectQueryBuilder adminSelectQueryBuilder;

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    PublicProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = "name";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("name", "name");
        mappedSortBy.put("price", "lowestPrice");
        mappedSortBy.put("sales", "totalSales");
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
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get("startAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get("startAt").as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get("endAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get("endAt").as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }
}
