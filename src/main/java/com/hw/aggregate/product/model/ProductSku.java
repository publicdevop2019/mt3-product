package com.hw.aggregate.product.model;

import com.hw.shared.StringSetConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSku {

    @Convert(converter = StringSetConverter.class)
    private Set<String> attributesSales;

    @NotNull
    @Column(updatable = false)
    private Integer storageOrder;

    @NotNull
    @Column(updatable = false)
    private Integer storageActual;

    @NotNull
    private BigDecimal price;

    @Column(updatable = false)
    private Integer sales;
}
