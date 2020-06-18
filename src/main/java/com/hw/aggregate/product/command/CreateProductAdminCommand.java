package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class CreateProductAdminCommand {
    private String imageUrlSmall;
    private String name;
    private Integer orderStorage;
    private Integer actualStorage;
    private String description;
    private String rate;
    private BigDecimal price;
    private Integer sales;
    private String catalog;
    private List<ProductOption> selectedOptions;
    private Set<String> imageUrlLarge;
    private Set<String> specification;
}
