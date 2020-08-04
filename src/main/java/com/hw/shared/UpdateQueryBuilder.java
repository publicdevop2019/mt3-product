package com.hw.shared;

import com.hw.aggregate.product.model.JsonPatchOperationLike;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class UpdateQueryBuilder<T> implements WhereClause<T> {
    protected EntityManager em;
    protected CriteriaBuilder cb;

    public Integer update(String search, List<JsonPatchOperationLike> likes, Class<T> clazz) {
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        Root<T> root = criteriaUpdate.from(clazz);
        Predicate whereClause = getWhereClause(root, search);

        if (whereClause != null)
            criteriaUpdate.where(whereClause);
        setUpdateValue(criteriaUpdate, likes);
        return em.createQuery(criteriaUpdate).executeUpdate();
    }

    protected abstract void setUpdateValue(CriteriaUpdate<T> criteriaUpdate, List<JsonPatchOperationLike> operationLikes);

}
