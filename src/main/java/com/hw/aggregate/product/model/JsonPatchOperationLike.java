package com.hw.aggregate.product.model;

import lombok.Data;

@Data
public class JsonPatchOperationLike {
    private String opt;
    private String path;
    private Object value;
}
