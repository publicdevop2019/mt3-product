package com.hw.aggregate.product.model;

import com.hw.shared.StringSetConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ProductSku {

    @Convert(converter = StringSetConverter.class)
    private Set<String> attributesSales;

    @NotNull
    @Column(updatable = false)
    private Integer storageOrder;

    @Id
    private Long productId;

    @NotNull
    @Column(updatable = false)
    private Integer storageActual;

    @NotNull
    private BigDecimal price;

    @Column(updatable = false)
    private Integer sales;
}
