package com.mt.mall.application.product.representation;

import com.mt.mall.domain.model.product.Product;
import lombok.Data;

@Data
public
class ProductCardRepresentation {
    private String id;
    private String name;
    private String coverImage;
    private Integer totalSales;
    private Long startAt;
    private Long endAt;
    private Integer version;

    public ProductCardRepresentation(Object obj) {
        Product product = (Product) obj;
        setId(product.getProductId().getDomainId());
        setName(product.getName());
        setCoverImage(product.getImageUrlSmall());
        setTotalSales(product.getTotalSales());
        setStartAt(product.getStartAt());
        setEndAt(product.getEndAt());
        setVersion(product.getVersion());
    }

}
