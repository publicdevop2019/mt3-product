package com.mt.mall.domain.model.product.event;

import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.sku.SkuId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ProductDeleted extends ProductEvent {
    private Set<SkuId> removeSkuCommands;
    private  String changeId;

    public ProductDeleted(ProductId productId, Set<SkuId> removeSkuCommands, String changeId) {
        super(productId);
        this.removeSkuCommands = removeSkuCommands;
        this.changeId = changeId;
    }
}
