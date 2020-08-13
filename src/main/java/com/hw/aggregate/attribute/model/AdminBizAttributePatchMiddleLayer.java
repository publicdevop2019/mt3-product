package com.hw.aggregate.attribute.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class AdminBizAttributePatchMiddleLayer extends TypedClass<AdminBizAttributePatchMiddleLayer> {

    private String name;

    private String description;

    private BizAttribute.AttributeMethod method;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;

    private BizAttribute.BizAttributeType type;

    public AdminBizAttributePatchMiddleLayer(BizAttribute bizAttribute) {
        super(AdminBizAttributePatchMiddleLayer.class);
        this.name = bizAttribute.getName();
        this.description = bizAttribute.getDescription();
        this.method = bizAttribute.getMethod();
        this.selectValues = bizAttribute.getSelectValues();
        this.type = bizAttribute.getType();
    }

    public AdminBizAttributePatchMiddleLayer() {
        super(AdminBizAttributePatchMiddleLayer.class);
    }
}
