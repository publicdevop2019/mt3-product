package com.hw.aggregate.product.model;

import com.hw.aggregate.product.exception.QueryNotFoundException;
import com.hw.shared.DeleteQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.product.model.ProductSku.SKU_PRODUCT_ID_LITERAL;
import static com.hw.aggregate.product.representation.ProductDetailAdminRep.ADMIN_REP_ID_LITERAL;

@Component
public class AdminProductSkuDeleteQueryBuilder extends DeleteQueryBuilder<ProductSku> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    @Override
    public Predicate getWhereClause(Root<ProductSku> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            throw new QueryNotFoundException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if (ADMIN_REP_ID_LITERAL.equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<ProductSku> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(SKU_PRODUCT_ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

}
