package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogTreeCustomerRepresentation {

    private List<CatalogSummaryCardRepresentation> data;


    public CatalogTreeCustomerRepresentation(List<Catalog> data) {
        this.data = data.stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CatalogSummaryCardRepresentation {
        private Long id;
        private String name;
        private Set<String> attributesKey;
        private Long parentId;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.name = catalog.getName();
            this.attributesKey = catalog.getAttributes();
            this.parentId = catalog.getParentId();
        }
    }
}
