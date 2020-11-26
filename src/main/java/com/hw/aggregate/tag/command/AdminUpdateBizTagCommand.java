package com.hw.aggregate.tag.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.aggregate.tag.model.BizTag;
import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class AdminUpdateBizTagCommand implements Serializable , AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private String name;
    private String description;
    private BizTag.AttributeMethod method;
    @JsonDeserialize(as= LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;
    private BizTag.BizAttributeType type;
    private Integer version;
}
