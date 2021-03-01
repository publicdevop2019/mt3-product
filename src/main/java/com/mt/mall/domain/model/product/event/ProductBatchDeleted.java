package com.mt.mall.domain.model.product.event;

import com.mt.mall.domain.model.product.ProductId;
import com.mt.mall.domain.model.sku.SkuId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ProductBatchDeleted extends ProductEvent {
    private Set<SkuId> removeSkuCommands;
    private  String changeId;

    public ProductBatchDeleted(Set<ProductId> productIds, Set<SkuId> removeSkuCommands, String changeId) {
        super(productIds);
        this.removeSkuCommands = removeSkuCommands;
        this.changeId = changeId;
    }
}
