package com.hw.aggregate.catalog.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizCatalogDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizCatalog> {
}
