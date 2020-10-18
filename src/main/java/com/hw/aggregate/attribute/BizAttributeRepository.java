package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.model.BizAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BizAttributeRepository extends JpaRepository<BizAttribute, Long> {
}
