package com.hw.entity;

import com.hw.clazz.ProductOption;
import com.hw.clazz.ProductOptionMapper;
import com.hw.converter.StringSetConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Products")
@Data
@SequenceGenerator(name = "productDetailId_gen", sequenceName = "productDetailId_gen", initialValue = 100)
public class ProductDetail extends ProductSimple {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productDetailId_gen")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 10000)
    @Convert(converter = ProductOptionMapper.class)
    private List<ProductOption> selectedOptions;


    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;
}
