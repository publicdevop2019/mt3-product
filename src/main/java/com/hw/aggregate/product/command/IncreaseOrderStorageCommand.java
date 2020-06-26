package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.StorageChangeDetail;
import lombok.Data;

import java.util.List;

@Data
public class IncreaseOrderStorageCommand {
    private String txId;
    private List<StorageChangeDetail> changeList;
}
