package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

import java.util.Set;

@Data
public class BizAttributeAdminRep {
    private String name;
    private String description;
    private BizAttribute.AttributeMethod method;
    private Set<String> selectValues;
    private BizAttribute.BizAttributeType type;

    public BizAttributeAdminRep(BizAttribute attribute) {
        this.name = attribute.getName();
        this.description = attribute.getDescription();
        this.selectValues = attribute.getSelectValues();
        this.method = attribute.getMethod();
        this.type = attribute.getType();
    }
}
