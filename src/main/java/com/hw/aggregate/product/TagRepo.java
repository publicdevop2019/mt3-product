package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepo extends JpaRepository<Tag, Long> {
    Optional<Tag> findByValue(String value);
}
