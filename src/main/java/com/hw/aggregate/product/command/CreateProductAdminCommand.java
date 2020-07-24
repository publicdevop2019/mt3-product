package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class CreateProductAdminCommand {
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private Long startAt;
    private Long endAt;
    private List<ProductOption> selectedOptions;
    private Set<String> specification;
    private Set<String> attributesKey;
    private Set<String> attributesProd;
    private Set<String> attributesGen;
    private List<CreateProductSkuAdminCommand> skus;
    private List<CreateProductAttrImageAdminCommand> attributeSaleImages;

    @Data
    public static class CreateProductSkuAdminCommand {
        private Set<String> attributesSales;
        private Integer storageOrder;
        private Integer storageActual;
        private BigDecimal price;
        private Integer sales;
    }

    @Data
    public static class CreateProductAttrImageAdminCommand {
        private String attributeSales;
        private List<String> imageUrls;
    }
}
