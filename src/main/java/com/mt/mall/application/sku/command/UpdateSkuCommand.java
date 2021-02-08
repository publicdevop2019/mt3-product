package com.mt.mall.application.sku.command;

import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class UpdateSkuCommand implements Serializable , AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private Integer version;
    private BigDecimal price;
    private String description;
}
