package com.hw.aggregate.sku.command;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AdminCreateBizSkuCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String referenceId;
    private String description;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
}
