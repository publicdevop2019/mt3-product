package com.mt.mall.port.adapter.persistence.catalog;

import com.mt.common.sql.clause.SelectFieldStringEqualClause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CatalogIdQueryClause<T> extends SelectFieldStringEqualClause<T> {
    public CatalogIdQueryClause(String fieldName) {
        super(fieldName);
    }

    @Override
    protected Predicate getExpression(String input, CriteriaBuilder cb, Root<T> root) {
        return cb.equal(root.get(entityFieldName).get("domainId").as(String.class), input);
    }

}
