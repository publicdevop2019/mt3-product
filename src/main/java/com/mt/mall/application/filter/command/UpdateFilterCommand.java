package com.mt.mall.application.filter.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateFilterCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private List<FilterItemCommand> filters;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> catalogs;
    private String description;
    private Integer version;

}
