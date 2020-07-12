package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

@Data
public class BizAttributeCreatedRepresentation {
    private Long id;

    public BizAttributeCreatedRepresentation(BizAttribute attribute) {
        this.id = attribute.getId();
    }
}
