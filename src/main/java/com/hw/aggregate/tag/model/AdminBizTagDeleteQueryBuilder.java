package com.hw.aggregate.tag.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizTagDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizTag> {
}
