package com.hw.aggregate.attribute.model;

import lombok.Data;

import java.util.Set;
@Data
public class BizAttributePatchMiddleLayer {

    private String name;

    private String description;

    private BizAttribute.AttributeMethod method;

    private Set<String> selectValues;

    private BizAttribute.BizAttributeType type;

    public BizAttributePatchMiddleLayer(BizAttribute bizAttribute) {
        this.name = bizAttribute.getName();
        this.description = bizAttribute.getDescription();
        this.method = bizAttribute.getMethod();
        this.selectValues = bizAttribute.getSelectValues();
        this.type = bizAttribute.getType();
    }
}
