package com.hw.aggregate.sku.representation;

import com.hw.aggregate.sku.model.BizSku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminBizSkuRep {
    private Long id;

    private String referenceId;

    private Integer storageOrder;

    private Integer storageActual;

    private BigDecimal price;

    private Integer sales;

    private String createdBy;

    private Date createdAt;

    private String modifiedBy;

    private Date modifiedAt;

    private String description;
    private Integer version;

    public AdminBizSkuRep(BizSku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
