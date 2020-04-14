package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class UpdateProductAdminCommand {
    private String imageUrlSmall;
    private String name;
    private Integer increaseOrderStorageBy;
    private Integer decreaseOrderStorageBy;
    private Integer increaseActualStorageBy;
    private Integer decreaseActualStorageBy;
    private String description;
    private String rate;
    private BigDecimal price;
    private Integer sales;
    private String category;
    private List<ProductOption> selectedOptions;
    private Set<String> imageUrlLarge;
    private Set<String> specification;

}
