package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.TagReferenceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagReferenceMapRepo extends JpaRepository<TagReferenceMap, Long> {
}
