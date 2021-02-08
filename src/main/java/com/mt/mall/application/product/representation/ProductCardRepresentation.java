package com.mt.mall.application.product.representation;

import com.mt.mall.domain.model.product.Product;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;

@Data
public
class ProductCardRepresentation {
    private Long id;
    private String name;
    private String coverImage;
    private Integer totalSales;
    private Long startAt;
    private Long endAt;
    private HashMap<String, Long> attrSalesMap;
    private Integer version;

    public ProductCardRepresentation(Product product) {
        BeanUtils.copyProperties(product, this);
        this.coverImage = product.getImageUrlSmall();
    }

}
