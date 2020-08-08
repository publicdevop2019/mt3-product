package com.hw.aggregate.product.representation;

import com.hw.shared.SumPagedRep;
import lombok.Data;

@Data
public class ProductAppSumPagedRep extends SumPagedRep<Void> {
    public ProductAppSumPagedRep(Long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }
}
