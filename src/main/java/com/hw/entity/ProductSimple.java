package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.shared.Auditable;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@Inheritance
@SequenceGenerator(name = "productSimpleId_gen", sequenceName = "productSimpleId_gen", initialValue = 100)
public class ProductSimple extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productSimpleId_gen")
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
    private String category;

}
