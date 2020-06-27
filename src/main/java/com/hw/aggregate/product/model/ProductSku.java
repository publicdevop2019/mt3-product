package com.hw.aggregate.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer storageOrder;
    @NotNull
    private Integer storageActual;
    @NotNull
    private BigDecimal price;
    private Integer sales;

    public ProductSku(Object attributesSales, Object storageOrder, Object storageActual, Object price, Object sales) {
        StringSetConverter stringSetConverter = new StringSetConverter();
        this.attributesSales = stringSetConverter.convertToEntityAttribute((String) attributesSales);
        this.storageOrder = (Integer) storageOrder;
        this.storageActual = (Integer) storageActual;
        this.price = (BigDecimal) price;
        this.sales = (Integer) sales;
    }
}
