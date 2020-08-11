package com.hw.aggregate.filter.model;

import com.hw.shared.DeleteQueryBuilder;
import com.hw.shared.WhereQueryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;


public class AdminFilterDeleteQueryBuilder extends DeleteQueryBuilder<BizFilter> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @Override
    public Predicate getWhereClause(Root<BizFilter> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            throw new WhereQueryNotFoundException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if (COMMON_ENTITY_ID.equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }


    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<BizFilter> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
