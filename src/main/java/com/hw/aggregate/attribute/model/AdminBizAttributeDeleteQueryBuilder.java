package com.hw.aggregate.attribute.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizAttributeDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizAttribute> {
}
