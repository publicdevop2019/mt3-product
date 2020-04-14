package com.hw.aggregate.product.representation;

import lombok.Data;

@Data
public class ProductCreatedRepresentation {
    private String id;

    public ProductCreatedRepresentation(String toString) {
        id = toString;
    }
}
