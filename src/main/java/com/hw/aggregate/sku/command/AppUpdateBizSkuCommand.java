package com.hw.aggregate.sku.command;

import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AppUpdateBizSkuCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private BigDecimal price;
    private Integer version;
}
