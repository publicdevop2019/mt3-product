package com.mt.mall.port.adapter.persistence.tag;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.mt.mall.domain.model.tag.Tag;
import org.springframework.stereotype.Component;

@Component
public class AdminBizTagDeleteQueryBuilder extends SoftDeleteQueryBuilder<Tag> {
}
