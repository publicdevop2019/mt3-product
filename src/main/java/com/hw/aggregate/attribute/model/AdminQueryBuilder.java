package com.hw.aggregate.attribute.model;

import com.hw.shared.SelectQueryBuilder;
import com.hw.shared.UnsupportedQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hw.aggregate.attribute.model.BizAttribute.NAME_LITERAL;
import static com.hw.aggregate.attribute.model.BizAttribute.TYPE_LITERAL;
import static com.hw.aggregate.product.model.Product.ID_LITERAL;


@Component("attributeAdmin")
public class AdminQueryBuilder extends SelectQueryBuilder<BizAttribute> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    public Predicate getWhereClause(Root<BizAttribute> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            return null;
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if ("id".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            } else {
                throw new UnsupportedQueryException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    AdminQueryBuilder() {
        DEFAULT_PAGE_SIZE = 200;
        MAX_PAGE_SIZE = 200;
        DEFAULT_SORT_BY = "id";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("id", ID_LITERAL);
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("type", TYPE_LITERAL);
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<BizAttribute> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
