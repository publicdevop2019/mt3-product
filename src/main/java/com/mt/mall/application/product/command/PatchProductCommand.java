package com.mt.mall.application.product.command;

import com.mt.common.domain.model.restful.TypedClass;
import com.mt.mall.domain.model.product.Product;
import lombok.Data;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class PatchProductCommand extends TypedClass<PatchProductCommand> {
    public PatchProductCommand() {
        super(PatchProductCommand.class);
    }

    private Long startAt;

    private Long endAt;
    private String name;

    public PatchProductCommand(Product productDetail) {
        super(PatchProductCommand.class);
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.name = productDetail.getName();
    }

}
