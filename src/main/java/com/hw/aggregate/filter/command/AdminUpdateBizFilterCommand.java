package com.hw.aggregate.filter.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class AdminUpdateBizFilterCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private List<BizFilterItemCommand> filters;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> catalogs;
    private String description;
    private Integer version;

    @Data
    public static class BizFilterItemCommand implements Serializable{
        private static final long serialVersionUID = 1;
        private Long id;
        private String name;
        @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
        private Set<String> values;
    }
}
