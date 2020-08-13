package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PublicProductCardRep {
    private Long id;
    private String name;
    private String imageUrlSmall;
    private String description;
    private BigDecimal lowestPrice;
    public static final String PUBLIC_REP_PRICE_LITERAL = "lowestPrice";
    private Integer totalSales;

    public PublicProductCardRep(Product productDetail) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.description = productDetail.getDescription();
        this.lowestPrice = productDetail.getLowestPrice();
        this.totalSales = productDetail.getTotalSales();
    }


}
