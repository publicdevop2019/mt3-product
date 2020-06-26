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
        private String name;
        private Set<String> attributesKey;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.name = catalog.getName();
            this.attributesKey =catalog.getAttributes();
        }
    }
}
