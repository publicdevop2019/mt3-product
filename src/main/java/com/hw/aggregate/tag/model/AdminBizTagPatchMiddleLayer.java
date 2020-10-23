package com.hw.aggregate.tag.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class AdminBizTagPatchMiddleLayer extends TypedClass<AdminBizTagPatchMiddleLayer> {

    private String name;

    private String description;

    private BizTag.AttributeMethod method;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;

    private BizTag.BizAttributeType type;

    public AdminBizTagPatchMiddleLayer(BizTag bizAttribute) {
        super(AdminBizTagPatchMiddleLayer.class);
        this.name = bizAttribute.getName();
        this.description = bizAttribute.getDescription();
        this.method = bizAttribute.getMethod();
        this.selectValues = bizAttribute.getSelectValues();
        this.type = bizAttribute.getType();
    }

    public AdminBizTagPatchMiddleLayer() {
        super(AdminBizTagPatchMiddleLayer.class);
    }
}
