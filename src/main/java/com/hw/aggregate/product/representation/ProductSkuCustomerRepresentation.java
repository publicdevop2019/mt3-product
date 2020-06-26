package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductSku;

import java.math.BigDecimal;
import java.util.Set;

public class ProductSkuCustomerRepresentation {
    private Set<String> attributeSales;
    private Integer storageOrder;
    private BigDecimal price;

    public ProductSkuCustomerRepresentation(ProductSku e) {
        this.attributeSales = e.getAttributeSales();
        this.storageOrder = e.getStorageOrder();
        this.price = e.getPrice();
    }
}
