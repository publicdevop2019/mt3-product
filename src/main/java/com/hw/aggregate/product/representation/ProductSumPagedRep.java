package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductSumPagedRep implements SumPagedRep<ProductDetail> {
    private List<ProductDetail> data = new ArrayList<>();
    private Long totalItemCount;

    public ProductSumPagedRep(List<ProductDetail> query, Long aLong) {
        this.data = query;
        this.totalItemCount = aLong;
    }
}
