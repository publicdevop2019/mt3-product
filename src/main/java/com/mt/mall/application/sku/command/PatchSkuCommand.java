package com.mt.mall.application.sku.command;

import com.mt.common.domain.model.restful.TypedClass;
import com.mt.mall.domain.model.sku.Sku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatchSkuCommand extends TypedClass<PatchSkuCommand> {

    private BigDecimal price;

    private String description;

    public PatchSkuCommand() {
        super(PatchSkuCommand.class);
    }


    public PatchSkuCommand(Sku bizSku) {
        super(PatchSkuCommand.class);
        this.price = bizSku.getPrice();
        this.description = bizSku.getDescription();
    }
}
