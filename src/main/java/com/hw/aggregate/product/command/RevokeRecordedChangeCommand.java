package com.hw.aggregate.product.command;

import lombok.Data;

@Data
public class RevokeRecordedChangeCommand {
    private String txId;

    public RevokeRecordedChangeCommand(String txId) {
        this.txId = txId;
    }
}
