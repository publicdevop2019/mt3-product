package com.hw.aggregate.sku.command;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AppCreateBizSkuCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;
}
