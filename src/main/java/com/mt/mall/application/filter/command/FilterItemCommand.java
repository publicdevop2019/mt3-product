package com.mt.mall.application.filter.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class FilterItemCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Long id;
    private String name;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> values;
}
