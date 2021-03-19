package com.mt.mall.domain.model.product.event;

import com.mt.mall.domain.model.product.ProductId;

public class ProductUpdated extends ProductEvent{
    public ProductUpdated(ProductId productId) {
        super(productId);
    }
}
