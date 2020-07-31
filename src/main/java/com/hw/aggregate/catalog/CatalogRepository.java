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
    default List<Catalog> query(EntityManager entityManager, Predicate predicate, PageRequest pageRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Catalog> query = cb.createQuery(Catalog.class);
        Root<Catalog> root = query.from(Catalog.class);
        query.select(root);
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

    default Long queryCount(EntityManager entityManager, Predicate predicate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Catalog> from = query.from(Catalog.class);
        query.select(cb.count(from));
        query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }
}
