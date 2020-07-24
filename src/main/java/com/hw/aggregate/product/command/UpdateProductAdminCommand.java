package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class UpdateProductAdminCommand {
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private Long endAt;
    private Long startAt;
    private List<ProductOption> selectedOptions;
    private Set<String> specification;
    private Set<String> attributesKey;
    private Set<String> attributesProd;
    private Set<String> attributesGen;
    private List<UpdateProductAdminSkuCommand> skus;
    private List<UpdateProductAttrImageAdminCommand> attributeSaleImages;

    @Data
    public static class UpdateProductAdminSkuCommand {
        private Integer decreaseActualStorage;
        private Integer decreaseOrderStorage;
        private Integer increaseActualStorage;
        private Integer increaseOrderStorage;
        private Set<String> attributesSales;
        private BigDecimal price;
        private Integer storageOrder;
        private Integer storageActual;
        private Integer sales;
    }

    @Data
    public static class UpdateProductAttrImageAdminCommand {
        private String attributeSales;
        private List<String> imageUrls;
    }
}
