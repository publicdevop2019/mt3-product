package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
@Data
public class ProductSkuCustomerRepresentation {
    private Set<String> attributeSales;
    private Integer storageOrder;
    private BigDecimal price;

    public ProductSkuCustomerRepresentation(ProductSku e) {
        this.attributeSales = e.getAttributesSales();
        this.storageOrder = e.getStorageOrder();
        this.price = e.getPrice();
    }
}
