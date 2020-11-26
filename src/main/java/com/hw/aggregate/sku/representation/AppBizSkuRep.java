package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppBizSkuRep {
    private BigDecimal price;
    private Integer version;
    public AppBizSkuRep(BizSku bizSku) {
        this.price = bizSku.getPrice();
        this.version = bizSku.getVersion();
    }
}
