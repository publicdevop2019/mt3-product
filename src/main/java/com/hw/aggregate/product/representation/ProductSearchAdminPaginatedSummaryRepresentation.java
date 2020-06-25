package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductSearchAdminPaginatedSummaryRepresentation {
    private List<ProductAdminCardRepresentation> data;
    private Integer totalPageCount;
    private Long totalProductCount;

    public ProductSearchAdminPaginatedSummaryRepresentation(List<ProductDetail> data) {
        this.data = data.stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList());
    }

    public ProductSearchAdminPaginatedSummaryRepresentation(List<ProductDetail> data, Integer totalPageCount, Long totalProductCount) {
        this.data = data.stream().map(ProductAdminCardRepresentation::new).collect(Collectors.toList());
        this.totalPageCount = totalPageCount;
        this.totalProductCount = totalProductCount;
    }

    @Data
    private static class ProductAdminCardRepresentation {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer sales;
        private Set<String> tags;
        private Integer orderStorage;
        private Integer actualStorage;

        public ProductAdminCardRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.price = productDetail.getPrice();
            this.sales = productDetail.getSales();
            this.tags = productDetail.getAttributes();
            this.orderStorage = productDetail.getOrderStorage();
            this.actualStorage = productDetail.getActualStorage();
        }

    }
}
