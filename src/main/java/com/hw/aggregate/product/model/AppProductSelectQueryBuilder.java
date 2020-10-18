package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AppProductSelectQueryBuilder extends SelectQueryBuilder<Product> {
    AppProductSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        defaultWhereField.add(new SelectStatusClause<>());
    }
}
