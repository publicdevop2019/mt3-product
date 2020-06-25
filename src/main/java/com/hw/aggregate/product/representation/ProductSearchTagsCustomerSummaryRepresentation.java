package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductSearchTagsCustomerSummaryRepresentation {
    private List<ProductCatalogRepresentation> productSimpleList;

    public ProductSearchTagsCustomerSummaryRepresentation(List<ProductDetail> productSimpleList) {
        this.productSimpleList = productSimpleList.stream().map(ProductCatalogRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private class ProductCatalogRepresentation {
        private Long id;
        private String imageUrlSmall;
        private String name;
        private String description;
        private String rate;
        private BigDecimal price;
        private Integer sales;
        private Set<String> tags;
        private Integer orderStorage;

        public ProductCatalogRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.name = productDetail.getName();
            this.description = productDetail.getName();
            this.rate = productDetail.getRate();
            this.price = productDetail.getPrice();
            this.sales = productDetail.getSales();
            this.tags = productDetail.getAttributes();
            this.orderStorage = productDetail.getOrderStorage();
        }

    }
}
