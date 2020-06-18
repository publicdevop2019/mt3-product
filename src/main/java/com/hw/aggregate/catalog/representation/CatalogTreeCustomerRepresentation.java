package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CatalogTreeCustomerRepresentation {

    private List<CatalogSummaryCardRepresentation> list;

    public CatalogTreeCustomerRepresentation(List<Catalog> list) {
        this.list = list.stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CatalogSummaryCardRepresentation {
        private String title;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.title = catalog.getTitle();
        }
    }
}
