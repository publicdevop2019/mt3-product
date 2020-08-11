package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

@Data
public class BizAttributeCreatedRep {
    private Long id;

    public BizAttributeCreatedRep(BizAttribute attribute) {
        this.id = attribute.getId();
    }
}
