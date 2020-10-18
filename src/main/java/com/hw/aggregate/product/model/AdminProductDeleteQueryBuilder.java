package com.hw.aggregate.product.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminProductDeleteQueryBuilder extends SoftDeleteQueryBuilder<Product> {

}
