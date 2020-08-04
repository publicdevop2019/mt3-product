package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductCustomerSumPagedRep implements SumPagedRep<ProductCustomerSumPagedRep.ProductSearchRepresentation> {
    private List<ProductSearchRepresentation> data = new ArrayList<>();
    private Long totalItemCount;

    public ProductCustomerSumPagedRep(List<ProductDetail> productSimpleList, Long totalItemCount) {
        this.data.addAll(productSimpleList.stream().map(ProductSearchRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = totalItemCount;
    }

    @Data
    protected static class ProductSearchRepresentation {
        private Long id;
        private String name;
        private String imageUrlSmall;
        private String description;
        private BigDecimal lowestPrice;
        private Integer totalSales;

        public ProductSearchRepresentation(ProductDetail productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.description = productDetail.getDescription();
            this.lowestPrice = productDetail.getLowestPrice();
            this.totalSales = productDetail.getTotalSales();
        }


    }
}
