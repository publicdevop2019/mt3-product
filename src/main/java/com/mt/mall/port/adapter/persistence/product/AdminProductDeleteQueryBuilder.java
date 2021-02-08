package com.mt.mall.port.adapter.persistence.product;

import com.mt.mall.domain.model.product.Product;
import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminProductDeleteQueryBuilder extends SoftDeleteQueryBuilder<Product> {

}
