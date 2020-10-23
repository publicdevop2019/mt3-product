package com.hw.aggregate.tag.representation;

import com.hw.aggregate.tag.model.BizTag;
import lombok.Data;

@Data
public class AppBizTagCardRep {
    private Long id;
    private String name;

    public AppBizTagCardRep(BizTag attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
    }
}