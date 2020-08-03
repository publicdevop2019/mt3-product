package com.hw.aggregate.product.model;

import lombok.Data;

@Data
public class JsonPatchOperationLike {
    private String op;
    private String path;
    private Object value;
}
