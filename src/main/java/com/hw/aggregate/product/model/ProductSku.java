package com.hw.aggregate.product.model;

import com.hw.shared.StringSetConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
@IdClass(ProductSku.PK.class)
public class ProductSku {
    @Id
    private Set<String> attributesSales;
    public transient static final String ATTR_SALES_LITERAL = "attributesSales";

    @NotNull
    @Column(updatable = false)
    private Integer storageOrder;
    public transient static final String STORAGE_ORDER_LITERAL = "storageOrder";

    @Id
    private Long productId;
    public transient static final String PRODUCT_ID_LITERAL = "productId";

    @NotNull
    @Column(updatable = false)
    private Integer storageActual;
    public transient static final String STORAGE_ACTUAL_LITERAL = "storageActual";

    @NotNull
    private BigDecimal price;
    public transient static final String PRICE_LITERAL = "price";

    @Column(updatable = false)
    private Integer sales;
    public transient static final String SALES_LITERAL = "sales";

    @Data
    public static class PK implements Serializable {
        public PK(){}
        @Column//do not delete
        @Convert(converter = StringSetConverter.class)
        private Set<String> attributesSales;
        private Long productId;
    }
}
