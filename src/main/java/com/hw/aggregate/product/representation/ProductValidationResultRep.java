package com.hw.aggregate.product.representation;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class ProductValidationResultRep {
    private Map<String, String> result = new HashMap<>();
    public ProductValidationResultRep(boolean containInvalidValue) {
        if (containInvalidValue) {
            result.put("result", "false");
        } else {
            result.put("result", "true");
        }
    }
}
