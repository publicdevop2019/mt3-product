package com.hw.aggregate.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"attributeSales", "productDetail"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSku {
    @ManyToOne
    private transient ProductDetail productDetail;
    @Convert(converter = StringSetConverter.class)
    private Set<String> attributeSales;
    @NotNull
    private Integer storageOrder;
    @NotNull
    private Integer storageActual;
    @NotNull
    private BigDecimal price;
    private Integer sales;
}
