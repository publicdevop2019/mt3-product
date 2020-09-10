package com.hw.aggregate.product.model;

import com.hw.shared.rest.TypedClass;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class AdminProductPatchMiddleLayer extends TypedClass<AdminProductPatchMiddleLayer> {
    public AdminProductPatchMiddleLayer() {
        super(AdminProductPatchMiddleLayer.class);
    }

    private Long startAt;

    private Long endAt;
    private String name;

    public AdminProductPatchMiddleLayer(Product productDetail) {
        super(AdminProductPatchMiddleLayer.class);
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.name = productDetail.getName();
    }

}
