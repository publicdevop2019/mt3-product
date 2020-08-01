package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.Catalog;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    default List<Catalog> query(EntityManager entityManager, CriteriaBuilder cb, CriteriaQuery<Catalog> query, Root<Catalog> root, Predicate predicate, PageRequest pageRequest) {
        query.select(root);
        if (predicate != null)
            query.where(predicate);
        Set<Order> collect = pageRequest.getSort().get().map(e -> {
            if (e.getDirection().isAscending()) {
                return cb.asc(root.get(e.getProperty()));
            } else {
                return cb.desc(root.get(e.getProperty()));
            }
        }).collect(Collectors.toSet());
        query.orderBy(collect.toArray(Order[]::new));
        TypedQuery<Catalog> query1 = entityManager.createQuery(query)
                .setFirstResult(BigDecimal.valueOf(pageRequest.getOffset()).intValue())
                .setMaxResults(pageRequest.getPageSize());
        return query1.getResultList();
    }

    default Long queryCount(EntityManager entityManager, CriteriaBuilder cb, Predicate predicate) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Catalog> from = query.from(Catalog.class);
        query.select(cb.count(from));
        if (predicate != null)
            query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }
}
