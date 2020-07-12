package com.hw.aggregate.attribute.command;

import com.hw.aggregate.attribute.model.AttributeMethod;
import com.hw.aggregate.attribute.model.BizAttributeType;
import lombok.Data;

import java.util.Set;

@Data
public class CreateBizAttributeCommand {
    private String name;
    private String description;
    private AttributeMethod method;
    private Set<String> selectValues;
    private BizAttributeType type;
}
