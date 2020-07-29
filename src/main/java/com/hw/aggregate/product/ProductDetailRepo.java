package com.hw.aggregate.product;

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
import java.time.Instant;
import java.util.*;
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

    default List<ProductDetail> searchByAttributesDynamic(EntityManager entityManager, String attributes, boolean customerSearch, PageRequest pageRequest) {
        if ("".equals(attributes) || attributes == null) {
            return new ArrayList<>(0);
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> root = query.from(ProductDetail.class);
        query.select(root);
        Predicate whereClause;
        Predicate attrWhereClause = getAttrWhereClause(attributes, cb, root);
        if (customerSearch) {
            Predicate statusClause = getStatusClause(cb, root);
            whereClause = cb.and(attrWhereClause, statusClause);
        } else {
            whereClause = attrWhereClause;
        }
        query.where(whereClause);
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

    default Long searchByAttributesDynamicCount(EntityManager entityManager, String attributes, boolean customerSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ProductDetail> from = query.from(ProductDetail.class);
        query.select(cb.count(from));
        Predicate whereClause;
        Predicate attrWhereClause = getAttrWhereClause(attributes, cb, from);
        if (customerSearch) {
            Predicate statusClause = getStatusClause(cb, from);
            whereClause = cb.and(attrWhereClause, statusClause);
        } else {
            whereClause = attrWhereClause;
        }
        query.where(whereClause);
        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate getStatusClause(CriteriaBuilder cb, Root<ProductDetail> root) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get("startAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get("startAt").as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get("endAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get("endAt").as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }

    private Predicate getAttrWhereClause(String attributes, CriteriaBuilder cb, Root<ProductDetail> root) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(attributes.split(",")));
        List<Predicate> list1 = strings.stream().filter(e -> !e.contains("$")).map(e -> getAndExpression(e, cb, root)).collect(Collectors.toList());
        List<Predicate> list2 = strings.stream().filter(e -> e.contains("$")).map(e -> getOrExpression(e, cb, root)).collect(Collectors.toList());
        list1.addAll(list2);
        return cb.and(list1.toArray(new Predicate[0]));
    }

    private Predicate getOrExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        String name = input.split(":")[0];
        String[] values = input.split(":")[1].split("\\$");
        Set<String> collect = Arrays.stream(values).map(el -> name + ":" + el).collect(Collectors.toSet());

        String[] strs = {"attrKey", "attrProd", "attrGen", "attrSalesTotal"};
        Predicate[] predicates = Arrays.stream(strs)
                .map(ee -> collect.stream().map(e -> cb.like(root.get(ee).as(String.class), "%" + e + "%")).collect(Collectors.toSet()))
                .flatMap(Collection::stream).distinct().toArray(Predicate[]::new);
        return cb.or(predicates);
    }

    private Predicate getAndExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        String[] strs = {"attrKey", "attrProd", "attrGen", "attrSalesTotal"};
        Predicate[] predicates = Arrays.stream(strs)
                .map(ee -> cb.like(root.get(ee).as(String.class), "%" + input + "%"))
                .collect(Collectors.toSet()).toArray(Predicate[]::new);
        return cb.or(predicates);
    }
}
