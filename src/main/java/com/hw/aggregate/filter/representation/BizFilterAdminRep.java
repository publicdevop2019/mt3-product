package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BizFilterAdminRep {
    private Long id;
    private Set<String> catalogs;
    private List<BizFilterItemAdminRepresentation> filters;

    public BizFilterAdminRep(BizFilter read) {
        this.id = read.getId();
        this.catalogs = read.getLinkedCatalog();
        this.filters = read.getFilterItems().stream().map(BizFilterItemAdminRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private static class BizFilterItemAdminRepresentation {
        private Long id;
        private String name;
        private Set<String> values;

        public BizFilterItemAdminRepresentation(BizFilter.BizFilterItem e) {
            this.id = e.getId();
            this.name = e.getName();
            this.values = e.getSelectValues();
        }
    }
}
