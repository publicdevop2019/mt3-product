package com.hw.aggregate.sku.model;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminBizSkuPatchMiddleLayer extends TypedClass<AdminBizSkuPatchMiddleLayer> {

    private BigDecimal price;

    private String description;

    public AdminBizSkuPatchMiddleLayer() {
        super(AdminBizSkuPatchMiddleLayer.class);
    }


    public AdminBizSkuPatchMiddleLayer(BizSku bizSku) {
        super(AdminBizSkuPatchMiddleLayer.class);
        this.price = bizSku.getPrice();
        this.description = bizSku.getDescription();
    }
}
