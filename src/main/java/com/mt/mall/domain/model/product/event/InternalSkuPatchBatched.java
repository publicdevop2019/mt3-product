package com.mt.mall.domain.model.product.event;

import com.mt.common.domain.model.restful.PatchCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InternalSkuPatchBatched extends ProductEvent {
    private List<PatchCommand> skuCommands;
    private String changeId;
}
