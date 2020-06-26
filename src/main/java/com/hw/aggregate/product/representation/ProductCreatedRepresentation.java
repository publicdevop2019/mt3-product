package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

@Data
public class ProductCreatedRepresentation {
    private String id;

    public ProductCreatedRepresentation(ProductDetail productDetail) {
        id = productDetail.getId().toString();
    }
}
