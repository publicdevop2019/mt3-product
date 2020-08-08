package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import lombok.Data;

@Data
public class ProductCreatedRep {
    private String id;

    public ProductCreatedRep(Product productDetail) {
        id = productDetail.getId().toString();
    }
}
