package com.hw.aggregate.product;

import com.hw.aggregate.product.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {

    @Query("SELECT p FROM #{#entityName} as p WHERE p.category = ?1")
    Page<ProductDetail> findProductByCategory(String categoryName, Pageable pageable);

    @Query("SELECT p FROM #{#entityName} as p WHERE p.name LIKE ?1%")
    List<ProductDetail> searchProductByName(String searchKey, Pageable pageable);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM #{#entityName} as p WHERE p.id = ?1")
    Optional<ProductDetail> findByIdForUpdate(Long id);
}
