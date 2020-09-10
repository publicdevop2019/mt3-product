package com.hw.aggregate.sku.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppCreateBizSkuCommand {
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;
}
