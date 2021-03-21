package com.mt.mall.domain.model.product;

import com.mt.mall.domain.model.tag.TagId;

import java.util.Optional;

public interface ProductTagRepository {
    Optional<ProductTag> findByTagIdAndTagValueAndType(TagId tagId, String value, TagType tagTypeEnum);
}
