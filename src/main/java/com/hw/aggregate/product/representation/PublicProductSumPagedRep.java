package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Data
public class PublicProductSumPagedRep extends SumPagedRep<PublicProductSumPagedRep.ProductCardRepresentation> {

    public PublicProductSumPagedRep(SumPagedRep<Product> select) {
        this.data.addAll(select.getData().stream().map(ProductCardRepresentation::new).collect(Collectors.toList()));
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    public static class ProductCardRepresentation {
        private Long id;
        private String name;
        private String imageUrlSmall;
        private String description;
        private BigDecimal lowestPrice;
        public static final String PUBLIC_REP_PRICE_LITERAL = "lowestPrice";
        private Integer totalSales;

        public ProductCardRepresentation(Product productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.description = productDetail.getDescription();
            this.lowestPrice = productDetail.getLowestPrice();
            this.totalSales = productDetail.getTotalSales();
        }


    }
}
