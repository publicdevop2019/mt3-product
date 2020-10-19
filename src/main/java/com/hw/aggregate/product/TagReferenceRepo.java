package com.hw.aggregate.product;

import com.hw.aggregate.product.model.TagReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagReferenceMapRepo extends JpaRepository<TagReference, Long> {
}
