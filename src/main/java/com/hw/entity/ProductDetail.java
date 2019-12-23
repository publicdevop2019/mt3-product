package com.hw.entity;

import com.hw.converter.MapMapConverter;
import com.hw.converter.StringSetConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Entity(name = "ProductDetail")
@Table(name = "ProductDetail")
@SequenceGenerator(name = "productDetailId_gen", sequenceName = "productDetailId_gen", initialValue = 100)
@Data
public class ProductDetail extends ProductSimple {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productDetailId_gen")
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @NotEmpty
    @Column
    @Convert(converter = MapMapConverter.class)
    private Map<String, Map<String, String>> productOptions;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;
}
