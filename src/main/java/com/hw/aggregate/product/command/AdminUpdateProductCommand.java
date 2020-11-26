package com.hw.aggregate.product.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class AdminUpdateProductCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private String changeId;
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
    private Integer version;
    @Data
    public static class UpdateProductAdminSkuCommand implements Serializable{
        private static final long serialVersionUID = 1;
        private Integer decreaseActualStorage;
        private Integer decreaseOrderStorage;
        private Integer increaseActualStorage;
        private Integer increaseOrderStorage;
        private Set<String> attributesSales;
        private BigDecimal price;
        private Integer storageOrder;//for new sku
        private Integer storageActual;
        private Integer sales;
        private Integer version;
    }

    @Data
    public static class UpdateProductAttrImageAdminCommand implements Serializable{
        private static final long serialVersionUID = 1;
        private String attributeSales;
        @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
        private Set<String> imageUrls;
    }
}
