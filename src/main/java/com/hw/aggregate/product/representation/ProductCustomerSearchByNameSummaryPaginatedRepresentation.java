package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.util.List;
@Data
public class ProductCustomerSearchByNameSummaryPaginatedRepresentation extends ProductCustomerSummaryPaginatedRepresentation {
    public ProductCustomerSearchByNameSummaryPaginatedRepresentation(List<ProductDetail> productSimpleList, Integer totalPageCount, Long totalProductCount) {
        super(productSimpleList, totalPageCount, totalProductCount);
    }
}
