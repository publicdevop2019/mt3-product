package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import lombok.Data;

import java.util.Set;

@Data
public
class AdminProductCardRep {
    private Long id;
    private String name;
    private String coverImage;
    private Integer totalSales;
    private Set<String> attributesKey;
    private Long startAt;
    private Long endAt;

    public AdminProductCardRep(Product productDetail) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.totalSales = productDetail.getTotalSales();
        this.attributesKey = productDetail.getAttrKey();
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.coverImage = productDetail.getImageUrlSmall();
    }

}
