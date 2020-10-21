package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldLongEqualClause<T> extends WhereClause<T> {
    public SelectFieldLongEqualClause(String fieldName) {
        entityFieldName = fieldName;
    }

    @Override
    public Predicate getWhereClause(String s, CriteriaBuilder cb, Root<T> root, Object query) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(entityFieldName), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
