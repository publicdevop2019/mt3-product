package com.hw.aggregate.product;

import com.hw.aggregate.product.model.AdminDeleteQueryBuilder;
import com.hw.aggregate.product.model.AdminUpdateQueryBuilder;
import com.hw.aggregate.product.model.JsonPatchOperationLike;
import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.name LIKE ?1% AND (start_at IS NOT NULL AND start_at <=?2 ) AND (end_at > ?2 OR end_at IS NULL)")
    Page<ProductDetail> searchProductByNameForCustomer(String searchKey, Long current, Pageable pageable);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageOrder = p.storageOrder - ?2 WHERE p.id = ?1 AND p.storageOrder - ?2 >= 0")
    Integer decreaseOrderStorage(Long id, Integer amountDecreased);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageOrder = p.storageOrder + ?2 WHERE p.id = ?1")
    Integer increaseOrderStorage(Long id, Integer amountIncreased);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual - ?2 , p.totalSales = p.totalSales + ?2 WHERE p.id = ?1 AND p.storageActual - ?2 >= 0")
    Integer decreaseActualStorageAndIncreaseSales(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual + ?2 , p.totalSales = p.totalSales - ?2 WHERE p.id = ?1 AND p.totalSales - ?2 >= 0")
    Integer increaseActualStorageAndDecreaseSales(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual - ?2 WHERE p.id = ?1 AND p.storageActual - ?2 >= 0")
    Integer decreaseActualStorage(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual + ?2 WHERE p.id = ?1 ")
    Integer increaseActualStorage(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.totalSales = p.totalSales + ?2 WHERE p.id = ?1 ")
    Integer increaseTotalSales(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.totalSales = p.totalSales - ?2 WHERE p.id = ?1 AND p.totalSales - ?2 >= 0")
    Integer decreaseTotalSales(Long id, Integer amount);

    default List<ProductDetail> query(EntityManager entityManager, CriteriaBuilder cb, CriteriaQuery<ProductDetail> query, Root<ProductDetail> root, Predicate predicate, PageRequest pageRequest) {
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

        TypedQuery<ProductDetail> query1 = entityManager.createQuery(query)
                .setFirstResult(BigDecimal.valueOf(pageRequest.getOffset()).intValue())
                .setMaxResults(pageRequest.getPageSize());
        return query1.getResultList();
    }

    default Integer update(EntityManager entityManager, CriteriaUpdate<ProductDetail> criteriaUpdate, Predicate predicate, List<JsonPatchOperationLike> operationLikes, AdminUpdateQueryBuilder adminUpdateQueryBuilder) {
        if (predicate != null)
            criteriaUpdate.where(predicate);
        adminUpdateQueryBuilder.setUpdateValue(criteriaUpdate, operationLikes);
        return entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    default Long queryCount(EntityManager entityManager, CriteriaBuilder cb, Predicate predicate) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ProductDetail> from = query.from(ProductDetail.class);
        query.select(cb.count(from));
        if (predicate != null)
            query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }

    default Integer delete(EntityManager entityManager, CriteriaDelete<?> criteriaDelete, Predicate predicate, AdminDeleteQueryBuilder adminDeleteQueryBuilder) {
        if (predicate != null)
            criteriaDelete.where(predicate);
        return entityManager.createQuery(criteriaDelete).executeUpdate();
    }
}
