package com.hw.aggregate.product.representation;

import com.hw.aggregate.product.model.*;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ProductDetailAdminRepresentation {
    private Long id;

    private String name;

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Set<String> specification;

    private List<ProductOption> selectedOptions;

    private Set<String> attrKey;

    private Set<String> attrProd;

    private Set<String> attrGen;

    private List<ProductSku> productSkuList;

    public ProductDetailAdminRepresentation(ProductDetail productDetail) {
        this.id = productDetail.getId();
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.description = productDetail.getDescription();
        this.selectedOptions = productDetail.getSelectedOptions();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.specification = productDetail.getSpecification();
        this.attrKey = productDetail.getAttrKey();
        this.attrProd = productDetail.getAttrProd();
        this.attrGen = productDetail.getAttrGen();
        this.productSkuList = productDetail.getProductSkuList();
    }
}
