package com.mt.mall.application.sku.representation;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class InternalSkuCardRepresentation {
    private Long id;

    private BigDecimal price;
    private Integer storageOrder;

    private Integer storageActual;

    private Integer sales;

    private Integer version;

    public InternalSkuCardRepresentation(Object bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
