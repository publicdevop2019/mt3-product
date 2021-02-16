package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalSkuCardRepresentation {
    private String id;

    private BigDecimal price;
    private Integer storageOrder;

    private Integer storageActual;

    private Integer sales;

    private Integer version;

    public InternalSkuCardRepresentation(Object obj) {
        Sku sku = (Sku) obj;
        setId(sku.getSkuId().getDomainId());
        setPrice(sku.getPrice());
        setStorageOrder(sku.getStorageOrder());
        setStorageActual(sku.getStorageActual());
        setSales(sku.getSales());
        setVersion(sku.getVersion());
    }
}
