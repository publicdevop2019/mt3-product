package com.hw.aggregate.product.model;

import com.hw.shared.sql.exception.UnsupportedQueryException;
import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.Product.*;

public class SelectFieldAttrLikeClause<T> extends WhereClause<T> {
    private final String[] attrs = {ATTR_KEY_LITERAL, ATTR_PROD_LITERAL, ATTR_GEN_LITERAL, ATTR_SALES_TOTAL_LITERAL};

    private Predicate getOrExpression(String input, CriteriaBuilder cb, Root<T> root) {
        if (input.split("-").length != 2)
            throw new UnsupportedQueryException();
        String name = input.split("-")[0];
        String[] values = input.split("-")[1].split("\\.");
        if (values.length == 1)
            throw new UnsupportedQueryException();
        Set<String> collect = Arrays.stream(values).map(el -> name + ":" + el).collect(Collectors.toSet());
        Predicate[] predicates = Arrays.stream(attrs)
                .map(ee -> collect.stream().map(e -> cb.like(root.get(ee).as(String.class), "%" + e + "%")).collect(Collectors.toSet()))
                .flatMap(Collection::stream).distinct().toArray(Predicate[]::new);
        return cb.or(predicates);
    }

    private Predicate getAndExpression(String input, CriteriaBuilder cb, Root<T> root) {
        if (input.split("-").length != 2)
            throw new UnsupportedQueryException();
        String replace = input.replace("-", ":");
        Predicate[] predicates = Arrays.stream(attrs)
                .map(ee -> cb.like(root.get(ee).as(String.class), "%" + replace + "%"))
                .collect(Collectors.toSet()).toArray(Predicate[]::new);
        return cb.or(predicates);
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(query.split("\\$")));
        List<Predicate> list1 = strings.stream().filter(e -> !e.contains("+")).map(e -> getAndExpression(e, cb, root)).collect(Collectors.toList());
        List<Predicate> list2 = strings.stream().filter(e -> e.contains("+")).map(e -> getOrExpression(e, cb, root)).collect(Collectors.toList());
        list1.addAll(list2);
        return cb.and(list1.toArray(new Predicate[0]));
    }
}
