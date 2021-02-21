package com.mt.mall.application.product.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class CreateProductCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String imageUrlSmall;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> imageUrlLarge;
    private String description;
    private Long startAt;
    private Long endAt;
    private List<ProductOptionCommand> selectedOptions;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> specification;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesKey;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesProd;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesGen;
    private List<CreateProductSkuAdminCommand> skus;
    private List<ProductSalesImageCommand> attributeSaleImages;

    private BigDecimal lowestPrice;
    private Integer totalSales;

    @Data
    public static class CreateProductSkuAdminCommand implements Serializable{
        private static final long serialVersionUID = 1;
        private Set<String> attributesSales;
        private Integer storageOrder;
        private Integer storageActual;
        private BigDecimal price;
        private Integer sales;
    }

}
