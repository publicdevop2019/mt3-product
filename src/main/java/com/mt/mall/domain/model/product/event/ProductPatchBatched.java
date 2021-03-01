package com.mt.mall.domain.model.product.event;

import com.mt.common.domain.model.restful.PatchCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class ProductPatchBatched extends ProductEvent {
    private List<PatchCommand> patchCommands;
    private  String changeId;

    public ProductPatchBatched(List<PatchCommand> patchCommands, String changeId) {
        super();
        this.patchCommands = patchCommands;
        this.changeId = changeId;
    }
}
