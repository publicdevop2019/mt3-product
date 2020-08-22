package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

@Data
public class AppBizAttributeCardRep {
    private Long id;
    private String name;

    public AppBizAttributeCardRep(BizAttribute attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
    }
}