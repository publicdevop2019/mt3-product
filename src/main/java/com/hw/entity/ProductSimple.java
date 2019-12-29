package com.hw.entity;

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


    @Column(nullable = false)
    private String imageUrlSmall;

    @NotNull
    @Column
    private String name;

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
