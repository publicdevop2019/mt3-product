package com.mt.mall.domain.model.product;

import java.util.Optional;

public interface ProductTagRepository {
    Optional<ProductTag> findByValueAndType(String value, TagType tagTypeEnum);
}
