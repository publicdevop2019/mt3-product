package com.mt.mall.domain.model.product;

import java.util.Optional;

public interface ProductTagRepository {
    Optional<ProductTag> findByTagValueAndType(String value, TagType tagTypeEnum);
}
