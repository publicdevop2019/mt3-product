package com.hw.aggregate.sku.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppUpdateBizSkuCommand {
    private BigDecimal price;
}
