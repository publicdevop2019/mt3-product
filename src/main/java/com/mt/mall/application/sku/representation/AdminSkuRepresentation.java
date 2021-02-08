package com.mt.mall.application.sku.representation;

import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminSkuRepresentation {
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

    public AdminSkuRepresentation(Sku bizSku) {
        BeanUtils.copyProperties(bizSku, this);
    }
}
