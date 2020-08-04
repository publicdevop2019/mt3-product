package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

@Data
public class ProductCreatedRep {
    private String id;

    public ProductCreatedRep(ProductDetail productDetail) {
        id = productDetail.getId().toString();
    }
}
