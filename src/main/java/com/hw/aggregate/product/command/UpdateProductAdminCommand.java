package com.hw.aggregate.product.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateProductAdminCommand {
    private String name;
    private String imageUrlSmall;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> imageUrlLarge;
    private String description;
    private Long endAt;
    private Long startAt;
    private List<ProductOption> selectedOptions;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> specification;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesKey;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesProd;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributesGen;
    private List<UpdateProductAdminSkuCommand> skus;
    private List<UpdateProductAttrImageAdminCommand> attributeSaleImages;
    private Integer decreaseActualStorage;
    private Integer decreaseOrderStorage;
    private Integer increaseActualStorage;
    private Integer increaseOrderStorage;
    private BigDecimal price;

    @Data
    public static class UpdateProductAdminSkuCommand {
        private Integer decreaseActualStorage;
        private Integer decreaseOrderStorage;
        private Integer increaseActualStorage;
        private Integer increaseOrderStorage;
        @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
        private Set<String> attributesSales;
        private BigDecimal price;
        private Integer storageOrder;//for new sku
        private Integer storageActual;
        private Integer sales;
    }

    @Data
    public static class UpdateProductAttrImageAdminCommand {
        private String attributeSales;
        @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
        private Set<String> imageUrls;
    }
}
