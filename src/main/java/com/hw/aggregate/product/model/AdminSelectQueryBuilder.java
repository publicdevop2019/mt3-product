package com.hw.aggregate.product.model;

import com.hw.shared.SelectQueryBuilder;
import com.hw.shared.UnsupportedQueryConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.ProductDetail.*;


@Component("productAdmin")
public class AdminSelectQueryBuilder extends SelectQueryBuilder<ProductDetail> {
    @Autowired
    private EntityManager em;
    @Autowired
    private CriteriaBuilder cb;

    private final String[] attrs = {ATTR_KEY_LITERAL, ATTR_PROD_LITERAL, ATTR_GEN_LITERAL, ATTR_SALES_TOTAL_LITERAL};

    AdminSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        DEFAULT_SORT_BY = "id";
        mappedSortBy = new HashMap<>();
        mappedSortBy.put("id", ID_LITERAL);
        mappedSortBy.put("name", NAME_LITERAL);
        mappedSortBy.put("sales", TOTAL_SALES_LITERAL);
        mappedSortBy.put("price", LOWEST_PRICE_LITERAL);
        mappedSortBy.put("expireDate", END_AT_LITERAL);
    }

    public Predicate getWhereClause(Root<ProductDetail> root, String search) {
        if (search == null)
            return null;
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if ("attr".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getAttrWhereClause(split[1], cb, root));
                }
                if ("name".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getNameWhereClause(split[1], cb, root));
                }
                if ("price".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getPriceWhereClause(split[1], cb, root));
                }
                if ("id".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<ProductDetail> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Predicate getNameWhereClause(String name, CriteriaBuilder cb, Root<ProductDetail> root) {
        return cb.like(root.get(NAME_LITERAL), "%" + name + "%");
    }

    private Predicate getPriceWhereClause(String s, CriteriaBuilder cb, Root<ProductDetail> root) {
        String[] split = s.split("\\$");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            if (str.contains("<=")) {
                int i = Integer.parseInt(s.replace("<=", ""));
                results.add(cb.lessThanOrEqualTo(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains(">=")) {
                int i = Integer.parseInt(s.replace(">=", ""));
                results.add(cb.greaterThanOrEqualTo(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains("<")) {
                int i = Integer.parseInt(s.replace("<", ""));
                results.add(cb.lessThan(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains(">")) {
                int i = Integer.parseInt(s.replace(">", ""));
                results.add(cb.greaterThan(root.get(LOWEST_PRICE_LITERAL), i));
            } else {
                throw new UnsupportedQueryConfigException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getAttrWhereClause(String attributes, CriteriaBuilder cb, Root<ProductDetail> root) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(attributes.split("\\$")));
        List<Predicate> list1 = strings.stream().filter(e -> !e.contains("+")).map(e -> getAndExpression(e, cb, root)).collect(Collectors.toList());
        List<Predicate> list2 = strings.stream().filter(e -> e.contains("+")).map(e -> getOrExpression(e, cb, root)).collect(Collectors.toList());
        list1.addAll(list2);
        return cb.and(list1.toArray(new Predicate[0]));
    }

    private Predicate getOrExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        if (input.split("-").length != 2)
            throw new UnsupportedQueryConfigException();
        String name = input.split("-")[0];
        String[] values = input.split("-")[1].split("\\.");
        if (values.length == 1)
            throw new UnsupportedQueryConfigException();
        Set<String> collect = Arrays.stream(values).map(el -> name + ":" + el).collect(Collectors.toSet());
        Predicate[] predicates = Arrays.stream(attrs)
                .map(ee -> collect.stream().map(e -> cb.like(root.get(ee).as(String.class), "%" + e + "%")).collect(Collectors.toSet()))
                .flatMap(Collection::stream).distinct().toArray(Predicate[]::new);
        return cb.or(predicates);
    }

    private Predicate getAndExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        if (input.split("-").length != 2)
            throw new UnsupportedQueryConfigException();
        String replace = input.replace("-", ":");
        Predicate[] predicates = Arrays.stream(attrs)
                .map(ee -> cb.like(root.get(ee).as(String.class), "%" + replace + "%"))
                .collect(Collectors.toSet()).toArray(Predicate[]::new);
        return cb.or(predicates);
    }
}
