package com.hw.aggregate.product.representation;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class ProductValidationResultRepresentation {
    private Map<String, String> result = new HashMap<>();
    public ProductValidationResultRepresentation(boolean containInvalidValue) {
        if (containInvalidValue) {
            result.put("result", "false");
        } else {
            result.put("result", "true");
        }
    }
}
