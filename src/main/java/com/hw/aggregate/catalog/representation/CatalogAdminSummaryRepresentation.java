package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogAdminSummaryRepresentation {

    private List<CatalogSummaryCardRepresentation> data;

    private Long totalItemCount;


    public CatalogAdminSummaryRepresentation(SumPagedRep<Catalog> select) {
        this.data = select.getData().stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    public static class CatalogSummaryCardRepresentation {
        private Long id;
        private String name;
        private Long parentId;
        private Set<String> attributes;
        private CatalogType catalogType;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.name = catalog.getName();
            this.parentId = catalog.getParentId();
            this.attributes = catalog.getAttributes();
            this.catalogType = catalog.getType();
        }
    }
}
