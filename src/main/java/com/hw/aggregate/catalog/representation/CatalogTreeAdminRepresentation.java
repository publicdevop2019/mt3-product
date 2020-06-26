package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogTreeAdminRepresentation {

    private List<CatalogSummaryCardRepresentation> data;

    public CatalogTreeAdminRepresentation(List<Catalog> data) {
        this.data = data.stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class CatalogSummaryCardRepresentation {
        private Long id;
        private String name;
        private Long parentId;
        private Set<String> attributesKey;
        private CatalogType catalogType;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.name = catalog.getName();
            this.parentId = catalog.getParentId();
            this.attributesKey = catalog.getAttributes();
            this.catalogType = catalog.getType();
        }
    }
}
