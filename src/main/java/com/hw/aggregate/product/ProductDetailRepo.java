package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {

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

}
