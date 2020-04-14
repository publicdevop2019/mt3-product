package com.hw.aggregate.product.command;

import lombok.Data;

import java.util.Map;

@Data
public class DecreaseOrderStorageCommand {
    private String optToken;
    private Map<String, String> productMap;

    public DecreaseOrderStorageCommand(Map<String, String> productMap, String optToken) {
        this.productMap = productMap;
        this.optToken = optToken;
    }
}
