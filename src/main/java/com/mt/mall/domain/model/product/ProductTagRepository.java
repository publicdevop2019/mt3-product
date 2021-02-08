package com.mt.mall.domain.model.product;

import com.mt.mall.domain.model.product.Tag;
import com.mt.mall.domain.model.product.TagTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByValueAndType(String value, TagTypeEnum tagTypeEnum);
}
