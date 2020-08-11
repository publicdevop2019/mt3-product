package com.hw.aggregate.catalog.model;

import com.hw.shared.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hw.aggregate.catalog.model.Catalog.*;


@Component
public class AdminCatalogSelectQueryBuilder extends SelectQueryBuilder<Catalog> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    public Predicate getWhereClause(Root<Catalog> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            return null;
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if ("type".equals(split[0]) && !split[1].isBlank()) {
                    results.add(cb.equal(root.get(TYPE_LITERAL).as(String.class), split[1]));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    AdminCatalogSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        mappedSortBy.put("name", NAME_LITERAL);
    }
}
