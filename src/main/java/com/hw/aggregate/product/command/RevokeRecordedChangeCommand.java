package com.hw.aggregate.product.command;

import lombok.Data;

@Data
public class RevokeRecordedChangeCommand {
    private String optToken;

    public RevokeRecordedChangeCommand(String optToken) {
        this.optToken = optToken;
    }
}
