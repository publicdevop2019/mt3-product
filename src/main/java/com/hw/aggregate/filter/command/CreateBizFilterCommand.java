package com.hw.aggregate.filter.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class CreateBizFilterCommand {
    private List<BizFilterItemCommand> filters;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> catalogs;

    @Data
    public static class BizFilterItemCommand {
        private Long id;
        private String name;
        @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
        private Set<String> values;
    }
}
