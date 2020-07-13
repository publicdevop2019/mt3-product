package com.hw.aggregate.product.representation;

import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.aggregate.product.exception.AttributeNameNotFoundException;
import com.hw.aggregate.product.model.ProductSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
public class ProductSkuCustomerRepresentation {
    private Set<String> attributeSales;
    private Integer storageOrder;
    private BigDecimal price;

    public ProductSkuCustomerRepresentation(ProductSku productSku) {
        this.attributeSales = productSku.getAttributesSales();
        this.storageOrder = productSku.getStorageOrder();
        this.price = productSku.getPrice();
    }

}
