package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class SkuRepresentation {
    private BigDecimal price;
    private Integer version;
    public SkuRepresentation(Sku sku) {
        setPrice(sku.getPrice());
        setVersion(sku.getVersion());
    }
}
