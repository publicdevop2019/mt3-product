package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AppBizSkuRep {
    private BigDecimal price;
    private Integer version;
    public AppBizSkuRep(BizSku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
