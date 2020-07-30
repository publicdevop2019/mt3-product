package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.AttributeMethod;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.BizAttributeType;
import lombok.Data;

import java.util.Set;

@Data
public class BizAttributeAdminRepresentation {
    private String name;
    private String description;
    private AttributeMethod method;
    private Set<String> selectValues;
    private BizAttributeType type;

    public BizAttributeAdminRepresentation(BizAttribute attribute) {
        this.name = attribute.getName();
        this.description = attribute.getDescription();
        this.selectValues = attribute.getSelectValues();
        this.method = attribute.getMethod();
        this.type = attribute.getType();
    }
}
