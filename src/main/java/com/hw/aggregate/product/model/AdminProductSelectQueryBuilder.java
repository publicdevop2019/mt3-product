package com.hw.aggregate.product.model;

import com.hw.shared.SelectQueryBuilder;
import com.hw.shared.UnsupportedQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.Product.*;
import static com.hw.aggregate.product.representation.AdminProductRep.*;
import static com.hw.aggregate.product.representation.PublicProductSumPagedRep.ProductCardRepresentation.PUBLIC_REP_PRICE_LITERAL;


@Component("productAdmin")
public class AdminProductSelectQueryBuilder extends SelectQueryBuilder<Product> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    private final String[] attrs = {ATTR_KEY_LITERAL, ATTR_PROD_LITERAL, ATTR_GEN_LITERAL, ATTR_SALES_TOTAL_LITERAL};

    AdminProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        DEFAULT_SORT_BY = ADMIN_REP_ID_LITERAL;
        mappedSortBy = new HashMap<>();
        mappedSortBy.put(ADMIN_REP_ID_LITERAL, ID_LITERAL);
        mappedSortBy.put(ADMIN_REP_NAME_LITERAL, NAME_LITERAL);
        mappedSortBy.put(ADMIN_REP_SALES_LITERAL, TOTAL_SALES_LITERAL);
        mappedSortBy.put(PUBLIC_REP_PRICE_LITERAL, LOWEST_PRICE_LITERAL);
        mappedSortBy.put(ADMIN_REP_END_AT_LITERAL, END_AT_LITERAL);
    }

    @Override
    public Predicate getWhereClause(Root<Product> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
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
            } else {
                throw new UnsupportedQueryException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<Product> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Predicate getNameWhereClause(String name, CriteriaBuilder cb, Root<Product> root) {
        return cb.like(root.get(NAME_LITERAL), "%" + name + "%");
    }

    private Predicate getPriceWhereClause(String s, CriteriaBuilder cb, Root<Product> root) {
        String[] split = s.split("\\$");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            if (str.contains("<:")) {
                int i = Integer.parseInt(s.replace("<:", ""));
                results.add(cb.lessThanOrEqualTo(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains(">:")) {
                int i = Integer.parseInt(s.replace(">:", ""));
                results.add(cb.greaterThanOrEqualTo(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains("<")) {
                int i = Integer.parseInt(s.replace("<", ""));
                results.add(cb.lessThan(root.get(LOWEST_PRICE_LITERAL), i));
            } else if (str.contains(">")) {
                int i = Integer.parseInt(s.replace(">", ""));
                results.add(cb.greaterThan(root.get(LOWEST_PRICE_LITERAL), i));
            } else {
                throw new UnsupportedQueryException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getAttrWhereClause(String attributes, CriteriaBuilder cb, Root<Product> root) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(attributes.split("\\$")));
        List<Predicate> list1 = strings.stream().filter(e -> !e.contains("+")).map(e -> getAndExpression(e, cb, root)).collect(Collectors.toList());
        List<Predicate> list2 = strings.stream().filter(e -> e.contains("+")).map(e -> getOrExpression(e, cb, root)).collect(Collectors.toList());
        list1.addAll(list2);
        return cb.and(list1.toArray(new Predicate[0]));
    }

    private Predicate getOrExpression(String input, CriteriaBuilder cb, Root<Product> root) {
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

    private Predicate getAndExpression(String input, CriteriaBuilder cb, Root<Product> root) {
        if (input.split("-").length != 2)
            throw new UnsupportedQueryException();
        String replace = input.replace("-", ":");
        Predicate[] predicates = Arrays.stream(attrs)
                .map(ee -> cb.like(root.get(ee).as(String.class), "%" + replace + "%"))
                .collect(Collectors.toSet()).toArray(Predicate[]::new);
        return cb.or(predicates);
    }
}
