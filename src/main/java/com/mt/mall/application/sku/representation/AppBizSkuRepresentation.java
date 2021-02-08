package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AppBizSkuRepresentation {
    private BigDecimal price;
    private Integer version;
    public AppBizSkuRepresentation(Sku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
