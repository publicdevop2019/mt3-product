package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class ProductCustomerSearchByAttributesSummaryPaginatedRepresentation extends ProductCustomerSummaryPaginatedRepresentation {
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(List<ProductDetail> productSimpleList, Integer pageSize, Long totalItemCount) {
        super(productSimpleList, pageSize, totalItemCount);
    }
}
