package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

@Data
public class BizAttributeAdminCreatedRep extends CreatedRep{
    private Long id;

    public BizAttributeAdminCreatedRep(BizAttribute attribute) {
        this.id = attribute.getId();
    }
}
