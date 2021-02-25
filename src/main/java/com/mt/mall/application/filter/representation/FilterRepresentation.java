package com.mt.mall.application.filter.representation;

import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterItem;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FilterRepresentation {
    private String id;
    private Set<String> catalogs;
    private List<FilterItemRepresentation> filters;
    private String description;
    private Integer version;

    public FilterRepresentation(Filter filter) {
        setId(filter.getFilterId().getDomainId());
        setDescription(filter.getDescription());
        setCatalogs(filter.getCatalogs());
        setVersion(filter.getVersion());
        this.filters = filter.getFilterItems().stream().map(FilterItemRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private static class FilterItemRepresentation {
        private String id;
        private String name;
        private Set<String> values;

        public FilterItemRepresentation(FilterItem e) {
            setId(e.getTagId());
            setName(e.getName());
            setValues(e.getValues());
        }
    }
}
