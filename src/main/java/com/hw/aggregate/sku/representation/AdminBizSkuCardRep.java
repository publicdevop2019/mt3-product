package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AdminBizSkuCardRep {
    private Long id;
    private String referenceId;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private String description;
    private Integer version;

    public AdminBizSkuCardRep(BizSku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
