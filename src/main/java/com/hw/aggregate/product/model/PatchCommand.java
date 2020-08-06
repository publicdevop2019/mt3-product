package com.hw.aggregate.product.model;

import lombok.Data;

@Data
public class PatchCommand {
    private String op;
    private String path;
    private Object value;
}
