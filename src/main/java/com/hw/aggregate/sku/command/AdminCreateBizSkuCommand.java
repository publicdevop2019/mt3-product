package com.hw.aggregate.sku.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminCreateBizSkuCommand {
    private String referenceId;
    private String description;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
}
