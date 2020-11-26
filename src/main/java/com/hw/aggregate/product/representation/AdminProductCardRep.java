package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.Tag;
import lombok.Data;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public
class AdminProductCardRep {
    private Long id;
    private String name;
    private String coverImage;
    private Integer totalSales;
    private Long startAt;
    private Long endAt;
    private HashMap<String, Long> attrSalesMap;
    private Integer version;

    public AdminProductCardRep(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.totalSales = product.getTotalSales();
        this.startAt = product.getStartAt();
        this.endAt = product.getEndAt();
        this.coverImage = product.getImageUrlSmall();
        this.attrSalesMap = product.getAttrSalesMap();
        this.version = product.getVersion();
    }

}
