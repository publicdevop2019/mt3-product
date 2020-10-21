package com.hw.shared.sql.builder;

import com.hw.shared.AuditorAwareImpl;
import com.hw.shared.sql.clause.SelectFieldIdWhereClause;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;
import static com.hw.shared.Auditable.*;

public abstract class SoftDeleteQueryBuilder<T> extends PredicateConfig<T> {
    @Autowired
    protected EntityManager em;

    protected SoftDeleteQueryBuilder() {
        supportedWhereField.put(COMMON_ENTITY_ID, new SelectFieldIdWhereClause());
    }

    public Integer delete(String search, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        Root<T> root = criteriaUpdate.from(clazz);
        List<String> collect = Arrays.stream(search.split(",")).collect(Collectors.toList());
        Predicate and = getPredicate(collect, cb, root,null);
        criteriaUpdate.where(and);
        criteriaUpdate.set(ENTITY_DELETED, true);
        Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
        criteriaUpdate.set(ENTITY_DELETED_BY, currentAuditor.orElse(""));
        criteriaUpdate.set(ENTITY_DELETED_AT, new Date());
        return em.createQuery(criteriaUpdate).executeUpdate();
    }


}
