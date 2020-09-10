package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppBizSkuCardRep {
    private Long id;

    private BigDecimal price;
    private Integer storageOrder;

    private Integer storageActual;

    private Integer sales;

    public AppBizSkuCardRep(BizSku bizSku) {
        this.id = bizSku.getId();
        this.price = bizSku.getPrice();
        this.storageOrder = bizSku.getStorageOrder();
        this.storageActual = bizSku.getStorageActual();
        this.sales = bizSku.getSales();
    }
}
