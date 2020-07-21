package com.hw.aggregate.product.command;

import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.aggregate.product.model.ProductStatus;
import lombok.Data;

import java.util.Date;
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
    private List<ProductSku> skus;
}
