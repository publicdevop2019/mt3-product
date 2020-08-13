package com.hw.aggregate.product.model;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class ProductPatchMiddleLayer extends TypedClass<ProductPatchMiddleLayer> {
    public ProductPatchMiddleLayer() {
        super(ProductPatchMiddleLayer.class);
    }

    private Long startAt;

    private Long endAt;
    private String name;

    public ProductPatchMiddleLayer(Product productDetail) {
        super(ProductPatchMiddleLayer.class);
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.name = productDetail.getName();
    }

}
