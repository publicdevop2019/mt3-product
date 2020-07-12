package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.name LIKE ?1% AND p.status = 'AVAILABLE'")
    Page<ProductDetail> searchProductByNameForCustomer(String searchKey, Pageable pageable);
}
