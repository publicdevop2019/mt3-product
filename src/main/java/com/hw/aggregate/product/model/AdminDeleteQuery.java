package com.hw.aggregate.product.model;

import com.hw.aggregate.product.exception.QueryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.aggregate.product.model.ProductDetail.ID_LITERAL;
import static com.hw.aggregate.product.model.ProductSku.PRODUCT_ID_LITERAL;

@Component
public class AdminDeleteQuery {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CriteriaBuilder cb;

    public Integer delete(String search) {
        //remove sku constrain first
        CriteriaDelete<ProductSku> criteriaDeleteSku = cb.createCriteriaDelete(ProductSku.class);
        Root<ProductSku> rootSku = criteriaDeleteSku.from(ProductSku.class);
        Predicate predicate = getSkuWhereClause(cb, rootSku, search);

        if (predicate != null)
            criteriaDeleteSku.where(predicate);
        entityManager.createQuery(criteriaDeleteSku).executeUpdate();

        CriteriaDelete<ProductDetail> criteriaDelete = cb.createCriteriaDelete(ProductDetail.class);
        Root<ProductDetail> root = criteriaDelete.from(ProductDetail.class);
        Predicate whereClause = getWhereClause(cb, root, search);
        if (predicate != null)
            criteriaDelete.where(whereClause);
        return entityManager.createQuery(criteriaDelete).executeUpdate();

    }

    private Predicate getWhereClause(CriteriaBuilder cb, Root<ProductDetail> root, String search) {
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

    private Predicate getSkuWhereClause(CriteriaBuilder cb, Root<ProductSku> root, String search) {
        if (search == null)
            throw new QueryNotFoundException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if ("id".equals(split[0]) && !split[1].isBlank()) {
                    results.add(getSkuWhereClause(split[1], cb, root));
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

    private Predicate getSkuWhereClause(String s, CriteriaBuilder cb, Root<ProductSku> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(PRODUCT_ID_LITERAL), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

}
