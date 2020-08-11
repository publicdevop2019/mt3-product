package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogAdminSummaryRep extends SumPagedRep<CatalogAdminSummaryRep.CatalogSummaryCardRepresentation> {

    public CatalogAdminSummaryRep(SumPagedRep<Catalog> select) {
        this.data = select.getData().stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    public static class CatalogSummaryCardRepresentation {
        private Long id;
        private String name;
        private Long parentId;
        private Set<String> attributes;
        private Catalog.CatalogType catalogType;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.name = catalog.getName();
            this.parentId = catalog.getParentId();
            this.attributes = catalog.getAttributes();
            this.catalogType = catalog.getType();
        }
    }
}
