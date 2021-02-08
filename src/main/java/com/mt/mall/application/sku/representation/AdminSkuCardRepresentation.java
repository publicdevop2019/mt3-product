package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AdminSkuCardRepresentation {
    private Long id;
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;
    private Integer version;

    public AdminSkuCardRepresentation(Object bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
