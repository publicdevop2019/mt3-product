package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;

import java.util.List;

public class ProductCustomerSearchByAttributesSummaryPaginatedRepresentation extends ProductCustomerSummaryPaginatedRepresentation {
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(List<ProductDetail> productSimpleList, Integer totalPageCount, Long totalProductCount) {
        super(productSimpleList, totalPageCount, totalProductCount);
    }
}
