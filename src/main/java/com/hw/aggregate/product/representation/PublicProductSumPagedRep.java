package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Data
public class PublicProductSumPagedRep extends SumPagedRep<PublicProductSumPagedRep.ProductSearchRepresentation> {

    public PublicProductSumPagedRep(SumPagedRep<Product> select) {
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

        public ProductSearchRepresentation(Product productDetail) {
            this.id = productDetail.getId();
            this.name = productDetail.getName();
            this.imageUrlSmall = productDetail.getImageUrlSmall();
            this.description = productDetail.getDescription();
            this.lowestPrice = productDetail.getLowestPrice();
            this.totalSales = productDetail.getTotalSales();
        }


    }
}
