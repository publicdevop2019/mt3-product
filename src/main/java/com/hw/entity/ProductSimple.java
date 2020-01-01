package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Inheritance
@Data
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
    private Integer storage;
    /**
     * use increase | decrease to make sure storage does not get overwritten
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer increaseStorageBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer decreaseStorageBy;

    @Column
    private String description;

    @Column
    private String rate;

    @NotNull
    @Column
    private BigDecimal price;

    @Column
    private BigDecimal sales;

    @NotNull
    @Column
    private String category;

}
