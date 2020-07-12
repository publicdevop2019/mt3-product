package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.util.List;
@Data
public class ProductAdminSearchByAttributesSummaryPaginatedRepresentation extends ProductAdminSummaryPaginatedRepresentation {
    public ProductAdminSearchByAttributesSummaryPaginatedRepresentation(List<ProductDetail> data, Integer totalPageCount, Long totalProductCount) {
        super(data, totalPageCount, totalProductCount);
    }
}
