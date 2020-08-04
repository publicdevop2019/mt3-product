package com.hw.aggregate.product.model;


import com.hw.aggregate.product.exception.QueryNotFoundException;
import com.hw.aggregate.product.exception.UpdateFieldNotFoundException;
import com.hw.aggregate.product.exception.UpdateFiledValueException;
import com.hw.shared.UpdateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.ProductDetail.*;

@Component
public class AdminUpdateQueryBuilder extends UpdateQueryBuilder<ProductDetail> {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CriteriaBuilder cb;

    protected Predicate getWhereClause(CriteriaBuilder cb, Root<ProductDetail> root, String search) {
        if (search == null)
            throw new QueryNotFoundException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if ("id".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    protected void setUpdateValue(CriteriaUpdate<ProductDetail> criteriaUpdate, List<JsonPatchOperationLike> operationLikes) {
        List<JsonPatchOperationLike> collect = operationLikes.stream().
                filter(e -> (e.getOp().equalsIgnoreCase("remove")
                        || e.getOp().equalsIgnoreCase("add")
                        || e.getOp().equalsIgnoreCase("replace"))
                        && (e.getPath().contains("startAt") || e.getPath().contains("endAt"))
                ).collect(Collectors.toList());
        if (collect.isEmpty())
            throw new UpdateFieldNotFoundException();
        collect.forEach(e -> {
            if (e.getPath().contains("endAt")) {
                if (e.getValue() != null) {
                    criteriaUpdate.set(END_AT_LITERAL, parseLong(e.getValue()));
                } else {
                    criteriaUpdate.set(END_AT_LITERAL, null);
                }
            }
            if (e.getPath().contains("startAt")) {
                if (e.getValue() != null) {
                    criteriaUpdate.set(START_AT_LITERAL, parseLong(e.getValue()));
                } else {
                    criteriaUpdate.set(START_AT_LITERAL, null);
                }
            }
        });
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<ProductDetail> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Long parseLong(Object input) {
        try {
            if (input.getClass().equals(Integer.class))
                return ((Integer) input).longValue();
            if (input.getClass().equals(BigInteger.class))
                return ((BigInteger) input).longValue();
            return Long.parseLong((String) input);
        } catch (NumberFormatException ex) {
            throw new UpdateFiledValueException();
        }
    }
}
