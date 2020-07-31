package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
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
public interface BizFilterRepository extends JpaRepository<BizFilter, Long> {
    default List<BizFilter> query(EntityManager entityManager, Predicate predicate, PageRequest pageRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BizFilter> query = cb.createQuery(BizFilter.class);
        Root<BizFilter> root = query.from(BizFilter.class);
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
        TypedQuery<BizFilter> query1 = entityManager.createQuery(query)
                .setFirstResult(BigDecimal.valueOf(pageRequest.getOffset()).intValue())
                .setMaxResults(pageRequest.getPageSize());
        return query1.getResultList();
    }

    default Long queryCount(EntityManager entityManager, Predicate predicate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<BizFilter> from = query.from(BizFilter.class);
        query.select(cb.count(from));
        query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }
}
