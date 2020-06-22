package com.hw.aggregate.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.shared.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table
@NoArgsConstructor
public class ProductDetail extends Auditable {

    @Id
    private Long id;

    @Column
    private String imageUrlSmall;

    @NotNull
    @Column
    private String name;

    @Column
    private Integer orderStorage;

    @Column
    private Integer actualStorage;
    /**
     * use increase | decrease to make sure storage does not get overwritten
     */
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer increaseOrderStorageBy;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer decreaseOrderStorageBy;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer increaseActualStorageBy;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer decreaseActualStorageBy;

    @Column
    private String description;

    @Column
    private String rate;

    @NotNull
    @Column
    private BigDecimal price;

    @Column
    private Integer sales;

    @NotNull
    @Column
    @Convert(converter = com.hw.aggregate.catalog.model.StringSetConverter.class)
    private Set<String> tags;

    @Column(length = 10000)
    @Convert(converter = ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;

    @Version
    private Integer version;

    public ProductDetail(Long id, String name, BigDecimal price, Integer sales, String tags, Integer orderStorage, Integer actualStorage) {
        this.id = id;
        this.name = name;
        this.orderStorage = orderStorage;
        this.actualStorage = actualStorage;
        this.price = price;
        this.sales = sales;
        HashSet<String> hashSet = new HashSet<>(Arrays.asList(tags.split(",")));
        this.tags = hashSet;
    }

    public static ProductDetail create(Long id, String imageUrlSmall, String name, Integer orderStorage, Integer actualStorage,
                                       String description, String rate, BigDecimal price,
                                       Integer sales, String catalog, List<ProductOption> selectedOptions,
                                       Set<String> imageUrlLarge, Set<String> specification) {
        return new ProductDetail(id, imageUrlSmall, name, orderStorage, actualStorage,
                description, rate, price, sales, catalog, selectedOptions, imageUrlLarge, specification);
    }

    public ProductDetail(Long id, String imageUrlSmall, String name, Integer orderStorage, Integer actualStorage,
                         String description, String rate, BigDecimal price,
                         Integer sales, String tags, List<ProductOption> selectedOptions,
                         Set<String> imageUrlLarge, Set<String> specification) {
        this.id = id;
        this.imageUrlSmall = imageUrlSmall;
        this.name = name;
        this.orderStorage = orderStorage;
        this.actualStorage = actualStorage;
        this.description = description;
        this.rate = rate;
        this.price = price;
        this.sales = sales == null ? 0 : sales;
//        this.tags = tags;
        this.selectedOptions = selectedOptions;
        this.imageUrlLarge = imageUrlLarge;
        this.specification = specification;
    }
}
