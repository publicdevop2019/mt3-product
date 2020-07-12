package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.util.List;
@Data
public class ProductAdminGetAllPaginatedSummaryRepresentation extends ProductAdminSummaryPaginatedRepresentation {
    public ProductAdminGetAllPaginatedSummaryRepresentation(List<ProductDetail> data, Integer totalPageCount, Long totalProductCount) {
        super(data, totalPageCount, totalProductCount);
    }
}
