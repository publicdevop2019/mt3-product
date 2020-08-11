package com.hw.aggregate.attribute.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.aggregate.attribute.model.BizAttribute;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class CreateBizAttributeCommand {
    private String name;
    private String description;
    private BizAttribute.AttributeMethod method;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;
    private BizAttribute.BizAttributeType type;
}
