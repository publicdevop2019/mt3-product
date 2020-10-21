package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SelectFieldStringLikeClause<T> extends WhereClause<T> {
    public SelectFieldStringLikeClause(String fieldName) {
        entityFieldName = fieldName;
    }


    protected Predicate getExpression(String input, CriteriaBuilder cb, Root<T> root) {
        return cb.like(root.get(entityFieldName).as(String.class), "%" + input + "%");
    }

    // type:PROD.SALES.GENERAL
    // type:PROD$SALES$GENERAL
    @Override
    public Predicate getWhereClause(String s, CriteriaBuilder cb, Root<T> root, Object query) {
        //sort before search
        if (s.contains(".")) {
            Set<String> strings = new TreeSet<>(Arrays.asList(s.split("\\.")));
            List<Predicate> list1 = strings.stream().map(e -> getExpression(e, cb, root)).collect(Collectors.toList());
            return cb.or(list1.toArray(Predicate[]::new));
        } else if (s.contains("$")) {
            Set<String> strings = new TreeSet<>(Arrays.asList(s.split("\\$")));
            List<Predicate> list2 = strings.stream().map(e -> getExpression(e, cb, root)).collect(Collectors.toList());
            return cb.and(list2.toArray(Predicate[]::new));
        } else {
            return getExpression(s, cb, root);
        }
    }
}
