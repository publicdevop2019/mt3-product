package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminBizSkuCardRep {
    private Long id;
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;

    public AdminBizSkuCardRep(BizSku bizSku) {
        this.id = bizSku.getId();
        this.referenceId = bizSku.getReferenceId();
        this.storageOrder = bizSku.getStorageOrder();
        this.storageActual = bizSku.getStorageActual();
        this.price = bizSku.getPrice();
        this.sales = bizSku.getSales();
        this.description= bizSku.getDescription();
    }
}
