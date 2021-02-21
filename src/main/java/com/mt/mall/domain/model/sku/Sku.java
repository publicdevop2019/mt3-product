package com.mt.mall.domain.model.sku;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.rest.exception.AggregateOutdatedException;
import com.mt.common.validate.Validator;
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

    private String description;
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

    public void replace(BigDecimal price, String description, Integer version) {
        if (!getVersion().equals(version)) {
            throw new AggregateOutdatedException();
        }
        setPrice(price);
        setDescription(description);
    }

    public void replace(BigDecimal price) {
        setPrice(price);
    }

    public void setDescription(String description) {
        Validator.whitelistOnly(description);
        Validator.lengthLessThanOrEqualTo(description, 50);
        this.description = description;
    }

    public void setStorageOrder(Integer storageOrder) {
        Validator.greaterThanOrEqualTo(storageOrder, 0);
        this.storageOrder = storageOrder;
    }

    public void setStorageActual(Integer storageActual) {
        Validator.greaterThanOrEqualTo(storageActual, 0);
        this.storageActual = storageActual;
    }

    public void setPrice(BigDecimal price) {
        Validator.greaterThan(price, BigDecimal.ZERO);
        this.price = price;
    }

    public void setSales(Integer sales) {
        Validator.greaterThanOrEqualTo(sales, 0);
        this.sales = sales;
    }
}
