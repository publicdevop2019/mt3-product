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
    private Map<String, String> skuIdMap;
    private Integer storageOrder;
    private BigDecimal price;

    public ProductSkuCustomerRepresentation(ProductSku productSku, BizAttributeSummaryRepresentation attributeSummaryRepresentation) {
        this.attributeSales = productSku.getAttributesSales();
        this.storageOrder = productSku.getStorageOrder();
        this.price = productSku.getPrice();
        this.skuIdMap = new HashMap<>();
        attributeSales.stream().map(e -> e.split(":")[0]).forEach(el -> skuIdMap.put(el, findName(el, attributeSummaryRepresentation)));
    }

    private String findName(String id, BizAttributeSummaryRepresentation attributeSummaryRepresentation) {
        Optional<BizAttributeSummaryRepresentation.BizAttributeCardRepresentation> first = attributeSummaryRepresentation.getData().stream().filter(e -> e.getId().toString().equals(id)).findFirst();
        if (first.isEmpty())
            throw new AttributeNameNotFoundException();
        return first.get().getName();
    }
}
