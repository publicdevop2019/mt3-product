package com.mt.mall.domain.model.sku;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.rest.exception.AggregateOutdatedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Entity
@Table
@NoArgsConstructor
public class Sku extends Auditable {
    public transient static final String SKU_REFERENCE_ID_LITERAL = "referenceId";
    public transient static final String SKU_STORAGE_ORDER_LITERAL = "storageOrder";
    public transient static final String SKU_STORAGE_ACTUAL_LITERAL = "storageActual";
    public transient static final String SKU_PRICE_LITERAL = "price";
    public transient static final String SKU_SALES_LITERAL = "sales";
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private String referenceId;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "skuId", updatable = false, nullable = false))
    })
    private SkuId skuId;

    @Setter(AccessLevel.PRIVATE)
    private String description;
    @NotNull
    @Column(updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Integer storageOrder;

    @NotNull
    @Column(updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Integer storageActual;

    @NotNull
    @Setter(AccessLevel.PRIVATE)
    private BigDecimal price;

    @Column(updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Integer sales;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public Sku(SkuId skuId, String referenceId, String description, Integer storageOrder, Integer storageActual, BigDecimal price, Integer sales) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
        setSkuId(skuId);
        setReferenceId(referenceId);
        setDescription(description);
        setStorageOrder(storageOrder);
        setStorageActual(storageActual);
        setPrice(price);
        setSales(sales);
    }

    public void replace(BigDecimal price, String description,Integer version) {
        if(!getVersion().equals(version)){
            throw new AggregateOutdatedException();
        }
        setPrice(price);
        setDescription(description);
    }

    public void replace(BigDecimal price) {
        setPrice(price);
    }
}
