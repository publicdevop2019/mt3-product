package com.mt.mall.application.sku.command;

import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.sku.SkuId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreateSkuCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private ProductId referenceId;
    private String description;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private SkuId skuId;
}
