package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.name LIKE ?1% AND (start_at IS NOT NULL AND start_at <=?2 ) AND (end_at > ?2 OR end_at IS NULL)")
    Page<ProductDetail> searchProductByNameForCustomer(String searchKey, Date current, Pageable pageable);
}
