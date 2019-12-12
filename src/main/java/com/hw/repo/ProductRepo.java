package com.hw.repo;

import com.hw.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<ProductDetail, Long> {
    @Query("SELECT p FROM ProductDetail p WHERE p.category = ?1")
    Optional<List<ProductDetail>> findProductByCategory(String categoryName);
}
