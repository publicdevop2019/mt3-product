package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface BizFilterRepository extends JpaRepository<BizFilter, Long> {
    default List<BizFilter> searchByAttributesDynamic(EntityManager entityManager, String catalog) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BizFilter> query = cb.createQuery(BizFilter.class);
        Root<BizFilter> root = query.from(BizFilter.class);
        query.select(root);
        Predicate linkedCatalog = cb.like(root.get("linkedCatalog").as(String.class), "%" + catalog + "%");
        query.where(linkedCatalog);
        TypedQuery<BizFilter> query1 = entityManager.createQuery(query);
        return query1.getResultList();
    }
}
