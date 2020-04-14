package com.hw.aggregate.product.command;

import lombok.Data;

import java.util.Map;

@Data
public class IncreaseOrderStorageCommand {
    private String optToken;
    private Map<String, String> productMap;

    public IncreaseOrderStorageCommand(Map<String, String> productMap, String optToken) {
        this.productMap = productMap;
        this.optToken = optToken;
    }
}
