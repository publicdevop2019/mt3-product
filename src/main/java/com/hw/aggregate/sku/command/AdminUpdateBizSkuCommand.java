package com.hw.aggregate.sku.command;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class AdminUpdateBizSkuCommand {
    private BigDecimal price;
    private String description;
}
