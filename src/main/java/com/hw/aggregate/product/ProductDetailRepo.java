package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {

//    @Query("SELECT p FROM #{#entityName} as p WHERE p.attrKey = ?1")
//    Page<ProductDetail> searchProductByAttributes(Set<String> tags, Pageable pageable);

    @Query("SELECT p FROM #{#entityName} as p WHERE p.name LIKE ?1%")
    Page<ProductDetail> searchProductByName(String searchKey, Pageable pageable);

//    @Modifying
//    @Query("UPDATE #{#entityName} as p SET p.orderStorage = p.orderStorage - ?2 WHERE p.id = ?1 AND p.orderStorage - ?2 >= 0")
//    Integer decreaseOrderStorage(Long id, Integer amountDecreased);

//    @Modifying
//    @Query("UPDATE #{#entityName} as p SET p.orderStorage = p.orderStorage + ?2 WHERE p.id = ?1")
//    Integer increaseOrderStorage(Long id, Integer amountIncreased);

//    @Modifying
//    @Query("UPDATE #{#entityName} as p SET p.actualStorage = p.actualStorage - ?2 , p.sales = p.sales + ?2 WHERE p.id = ?1 AND p.actualStorage - ?2 >= 0")
//    Integer decreaseActualStorageAndIncreaseSales(Long id, Integer amount);

//    @Modifying
//    @Query("UPDATE #{#entityName} as p SET p.actualStorage = p.actualStorage + ?2 , p.sales = p.sales - ?2 WHERE p.id = ?1 AND p.sales - ?2 >= 0")
//    Integer increaseActualStorageAndDecreaseSales(Long id, Integer amount);
}
