package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AppBizSkuCardRep {
    private Long id;

    private BigDecimal price;
    private Integer storageOrder;

    private Integer storageActual;

    private Integer sales;
    private Integer version;

    public AppBizSkuCardRep(BizSku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
