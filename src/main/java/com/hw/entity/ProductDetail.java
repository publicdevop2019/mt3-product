package com.hw.entity;

import com.hw.clazz.ProductOption;
import com.hw.clazz.ProductOptionMapper;
import com.hw.converter.StringSetConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
public class ProductDetail extends ProductSimple {

    @Column(length = 10000)
    @Convert(converter = ProductOptionMapper.class)
    private List<ProductOption> selectedOptions;


    @Column(nullable = false)
    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;
}
