package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductTotalSummaryPaginatedRepresentation {
    private List<ProductTotalAdminRepresentation> productSimpleList;
    private Integer totalPageCount;
    private Long totalProductCount;

    public ProductTotalSummaryPaginatedRepresentation(List<ProductDetail> productSimpleList, Integer totalPageCount, Long totalProductCount) {
        this.productSimpleList = productSimpleList.stream().map(e -> new ProductTotalAdminRepresentation(e)).collect(Collectors.toList());
        this.totalPageCount = totalPageCount;
        this.totalProductCount = totalProductCount;
    }

    @Data
    private class ProductTotalAdminRepresentation {
        private Long id;


        private String name;
        private Integer orderStorage;
        private Integer actualStorage;
        private String catalog;

        public ProductTotalAdminRepresentation(ProductDetail e) {
            this.id = e.getId();
            this.name = e.getName();
            this.orderStorage = e.getOrderStorage();
            this.actualStorage = e.getActualStorage();
            this.catalog = e.getCatalog();

        }
    }
}
