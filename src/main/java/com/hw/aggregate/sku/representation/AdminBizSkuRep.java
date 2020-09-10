package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminBizSkuRep {
    private Long id;

    private String referenceId;

    private Integer storageOrder;

    private Integer storageActual;

    private BigDecimal price;

    private Integer sales;

    private String createdBy;

    private Date createdAt;

    private String modifiedBy;

    private Date modifiedAt;

    private String description;

    public AdminBizSkuRep(BizSku bizSku) {
        this.id = bizSku.getId();
        this.referenceId = bizSku.getReferenceId();
        this.storageOrder = bizSku.getStorageOrder();
        this.storageActual = bizSku.getStorageActual();
        this.price = bizSku.getPrice();
        this.sales = bizSku.getSales();
        this.createdBy = bizSku.getCreatedBy();
        this.createdAt = bizSku.getCreatedAt();
        this.modifiedBy = bizSku.getModifiedBy();
        this.modifiedAt = bizSku.getModifiedAt();
        this.description = bizSku.getDescription();
    }
}
