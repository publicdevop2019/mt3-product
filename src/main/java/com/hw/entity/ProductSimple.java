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
public class ProductSimple extends Auditable {
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String imageUrlSmall;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String description;

    @NotNull
    @Column
    private String rate;

    @NotNull
    @Column
    private BigDecimal price;

    @NotNull
    @Column
    private BigDecimal sales;

    @NotNull
    @Column
    private String category;

}
