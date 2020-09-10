package com.hw.aggregate.sku.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.hw.shared.sql.exception.EmptyWhereClauseException;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;
import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class AdminBizSkuDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizSku> {

    @Override
    protected Predicate getWhereClause(Root<BizSku> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            throw new EmptyWhereClauseException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if (COMMON_ENTITY_ID.equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
                if (SKU_REFERENCE_ID_LITERAL.equals(split[0]) && !split[1].isBlank()) {
                    results.add(getRefIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getRefIdWhereClause(String s, CriteriaBuilder cb, Root<BizSku> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(SKU_REFERENCE_ID_LITERAL), str));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<BizSku> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
