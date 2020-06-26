package com.hw.aggregate.product.model;

import lombok.Data;

import java.util.Set;

@Data
public class StorageChangeDetail implements Comparable<StorageChangeDetail> {
    private Long productId;
    private Set<String> attributeSales;
    private Integer amount;

    @Override
    public int compareTo(StorageChangeDetail to) {
        if (productId.equals(to.productId))
            return 0;
        else if (productId > to.productId)
            return 1;
        else
            return -1;
    }
}
