package com.hw.aggregate.attribute.command;

import com.hw.aggregate.attribute.model.MethodEnum;
import lombok.Data;

import java.util.Set;

@Data
public class CreateBizAttributeCommand {
    private String name;
    private MethodEnum method;
    private Set<String> selectValues;
}
