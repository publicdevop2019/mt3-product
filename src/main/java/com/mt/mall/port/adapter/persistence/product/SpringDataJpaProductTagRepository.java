package com.mt.mall.port.adapter.persistence.product;

import com.mt.mall.domain.model.product.ProductTagRepository;
import com.mt.mall.domain.model.product.ProductTag;
import com.mt.mall.domain.model.product.TagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaProductTagRepository extends ProductTagRepository, JpaRepository<ProductTag, Long> {
    Optional<ProductTag> findByTagValueAndType(String value, TagType tagTypeEnum);
}