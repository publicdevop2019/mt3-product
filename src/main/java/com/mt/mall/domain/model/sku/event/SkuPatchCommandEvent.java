package com.mt.mall.domain.model.sku.event;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.mall.domain.model.product.event.ProductEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SkuPatchCommandEvent extends ProductEvent {
    private List<PatchCommand> skuCommands;
    private String changeId;
    private long taskId;
}
