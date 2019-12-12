package com.hw.entity;

import com.hw.converter.MapConverter;
import com.hw.converter.StringSetConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "ProductDetail")
@SequenceGenerator(name = "productDetailId_gen", sequenceName = "productDetailId_gen", initialValue = 100)
@Data
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productDetailId_gen")
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageLargeUrl;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageSmallUrl;

    @NotNull
    @NotEmpty
    @Column
    @Convert(converter = MapConverter.class)
    private Map<String, Map<String, String>> productOptions;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String description;

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
