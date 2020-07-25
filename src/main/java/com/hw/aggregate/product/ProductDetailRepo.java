package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

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
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual - ?2 , p.sales = p.sales + ?2 WHERE p.id = ?1 AND p.storageActual - ?2 >= 0")
    Integer decreaseActualStorageAndIncreaseSales(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual + ?2 , p.sales = p.sales - ?2 WHERE p.id = ?1 AND p.sales - ?2 >= 0")
    Integer increaseActualStorageAndDecreaseSales(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual - ?2 WHERE p.id = ?1 AND p.storageActual - ?2 >= 0")
    Integer decreaseActualStorage(Long id, Integer amount);

    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.storageActual = p.storageActual + ?2 WHERE p.id = ?1 ")
    Integer increaseActualStorage(Long id, Integer amount);
}
