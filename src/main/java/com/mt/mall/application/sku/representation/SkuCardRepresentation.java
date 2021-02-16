package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuCardRepresentation {
    private String id;
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;
    private Integer version;

    public SkuCardRepresentation(Object obj) {
        Sku sku = (Sku) obj;
        setId(sku.getSkuId().getDomainId());
        setReferenceId(sku.getReferenceId());
        setStorageOrder(sku.getStorageOrder());
        setStorageActual(sku.getStorageActual());
        setPrice(sku.getPrice());
        setSales(sku.getSales());
        setDescription(sku.getDescription());
        setVersion(sku.getVersion());
    }
}
