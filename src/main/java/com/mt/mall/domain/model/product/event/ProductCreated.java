package com.mt.mall.domain.model.product.event;

import com.mt.mall.application.sku.command.CreateSkuCommand;
import com.mt.mall.domain.model.product.ProductId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductCreated extends ProductEvent {
    private  List<CreateSkuCommand> createSkuCommands;
    private  String changeId;

    public ProductCreated(ProductId productId, List<CreateSkuCommand> createSkuCommands, String changeId) {
        super(productId);
        this.createSkuCommands = createSkuCommands;
        this.changeId = changeId;
    }
}
