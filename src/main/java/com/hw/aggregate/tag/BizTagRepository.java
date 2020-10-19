package com.hw.aggregate.tag;

import com.hw.aggregate.tag.model.BizTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BizTagRepository extends JpaRepository<BizTag, Long> {
}
