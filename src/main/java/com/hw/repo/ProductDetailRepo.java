package com.hw.repo;

import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Long> {
    @Query("SELECT p FROM #{#entityName} as p WHERE p.category = ?1")
    Optional<List<ProductDetail>> findProductByCategory(String categoryName);
}
