package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductSearchResultRepresentation {
    private List<ProductSearchRepresentation> productSearchRepresentations;

    public ProductSearchResultRepresentation(List<ProductDetail> productSimpleList) {
        this.productSearchRepresentations = productSimpleList.stream().map(ProductSearchResultRepresentation.ProductSearchRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private class ProductSearchRepresentation {
        private Long id;
        private String imageUrlSmall;
        private String name;
        private String description;
        private String rate;
        private BigDecimal price;
        private Integer sales;
        private String category;

        public ProductSearchRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.name = productDetail.getName();
            this.description = productDetail.getName();
            this.rate = productDetail.getRate();
            this.price = productDetail.getPrice();
            this.sales = productDetail.getSales();
            this.category = productDetail.getCategory();
        }

    }
}
