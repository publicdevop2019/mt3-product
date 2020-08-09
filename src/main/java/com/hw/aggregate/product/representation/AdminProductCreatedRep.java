package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import lombok.Data;

@Data
public class AdminProductCreatedRep {
    private String id;

    public AdminProductCreatedRep(Product productDetail) {
        id = productDetail.getId().toString();
    }
}
