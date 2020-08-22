package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.model.BizAttribute;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface BizAttributeRepository extends JpaRepository<BizAttribute, Long> {
}
