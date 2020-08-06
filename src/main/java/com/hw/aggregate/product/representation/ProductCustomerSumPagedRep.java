package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.ProductDetail;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Data
public class ProductCustomerSumPagedRep extends SumPagedRep<ProductCustomerSumPagedRep.ProductSearchRepresentation> {

    public ProductCustomerSumPagedRep(SumPagedRep<ProductDetail> select) {
        this.data.addAll(select.getData().stream().map(ProductSearchRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = select.getTotalItemCount();
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
