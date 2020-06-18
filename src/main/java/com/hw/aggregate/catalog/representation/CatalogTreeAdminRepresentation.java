package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CatalogTreeAdminRepresentation {

    private List<CatalogSummaryCardRepresentation> list;

    public CatalogTreeAdminRepresentation(List<Catalog> list) {
        this.list = list.stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CatalogSummaryCardRepresentation {
        private Long id;
        private String title;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.title = catalog.getTitle();
        }
    }
}
