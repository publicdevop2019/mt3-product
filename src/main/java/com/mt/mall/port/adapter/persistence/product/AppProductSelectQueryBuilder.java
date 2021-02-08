package com.mt.mall.port.adapter.persistence.product;

import com.mt.mall.domain.model.product.Product;
import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AppProductSelectQueryBuilder extends SelectQueryBuilder<Product> {
    {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        defaultWhereField.add(new SelectStatusClause());
    }
}
