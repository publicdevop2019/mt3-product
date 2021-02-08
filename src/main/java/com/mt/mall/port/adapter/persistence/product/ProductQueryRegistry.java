package com.mt.mall.port.adapter.persistence.product;

import com.mt.mall.domain.model.product.Product;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryRegistry extends RestfulQueryRegistry<Product> {

    @Override
    public Class<Product> getEntityClass() {
        return Product.class;
    }
}
