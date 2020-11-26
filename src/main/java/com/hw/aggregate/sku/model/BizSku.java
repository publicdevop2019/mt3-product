package com.hw.aggregate.sku.model;

import com.hw.aggregate.sku.command.AdminCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AdminUpdateBizSkuCommand;
import com.hw.aggregate.sku.command.AppCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AppUpdateBizSkuCommand;
import com.hw.shared.Auditable;
import com.hw.shared.rest.Aggregate;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class BizSku extends Auditable implements Aggregate {
    @Id
    private Long id;

    @Column(nullable = false)
    private String referenceId;
    public transient static final String SKU_REFERENCE_ID_LITERAL = "referenceId";

    private String description;
    @NotNull
    @Column(updatable = false)
    private Integer storageOrder;
    public transient static final String SKU_STORAGE_ORDER_LITERAL = "storageOrder";

    @NotNull
    @Column(updatable = false)
    private Integer storageActual;
    public transient static final String SKU_STORAGE_ACTUAL_LITERAL = "storageActual";

    @NotNull
    private BigDecimal price;
    public transient static final String SKU_PRICE_LITERAL = "price";

    @Column(updatable = false)
    private Integer sales;
    public transient static final String SKU_SALES_LITERAL = "sales";
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public static BizSku create(Long id, AdminCreateBizSkuCommand command) {
        return new BizSku(id, command);
    }

    public static BizSku create(Long id, AppCreateBizSkuCommand command) {
        return new BizSku(id, command);
    }

    private BizSku(Long id, AppCreateBizSkuCommand command) {
        this.id = id;
        this.referenceId = command.getReferenceId();
        this.storageOrder = command.getStorageOrder();
        this.storageActual = command.getStorageActual();
        this.price = command.getPrice();
        this.sales = command.getSales();
        this.description = command.getDescription();
    }

    private BizSku(Long id, AdminCreateBizSkuCommand command) {
        this.id = id;
        this.referenceId = command.getReferenceId();
        this.storageOrder = command.getStorageOrder();
        this.storageActual = command.getStorageActual();
        this.price = command.getPrice();
        this.sales = command.getSales();
        this.description = command.getDescription();
    }

    public BizSku replace(AdminUpdateBizSkuCommand command) {
        this.price = command.getPrice();
        this.description = command.getDescription();
        return this;
    }

    public BizSku replace(AppUpdateBizSkuCommand command) {
        this.price = command.getPrice();
        return this;
    }
}
