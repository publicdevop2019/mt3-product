package com.mt.mall.application.tag.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UpdateTagCommand implements Serializable{
    private static final long serialVersionUID = 1;
    private String name;
    private String description;
    private TagValueType method;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;
    private Type type;
    private Integer version;
}
