package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class WhereClause<T> {
    protected String entityFieldName;

    public abstract Predicate getWhereClause(String str, CriteriaBuilder cb, Root<T> root, Object query);

}
