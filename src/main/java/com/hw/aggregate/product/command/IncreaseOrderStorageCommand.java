package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.StorageChangeCommon;
import com.hw.aggregate.product.model.StorageChangeDetail;
import lombok.Data;

import java.util.List;

@Data
public class IncreaseOrderStorageCommand extends StorageChangeCommon {
}
