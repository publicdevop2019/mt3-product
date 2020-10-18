package com.hw.aggregate.product.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryRegistry extends RestfulQueryRegistry<Product> {

    @Override
    public Class<Product> getEntityClass() {
        return Product.class;
    }
}
