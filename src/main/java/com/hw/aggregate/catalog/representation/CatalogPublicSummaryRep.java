package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CatalogPublicSummaryRep extends SumPagedRep<CatalogPublicSummaryRep.CatalogSummaryCardRepresentation> {

    public CatalogPublicSummaryRep(SumPagedRep<Catalog> select) {
        this.data = select.getData().stream().map(CatalogSummaryCardRepresentation::new).collect(Collectors.toList());
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    public static class CatalogSummaryCardRepresentation {
        private Long id;
        private String name;
        private Set<String> attributes;
        private Long parentId;

        public CatalogSummaryCardRepresentation(Catalog catalog) {
            this.id = catalog.getId();
            this.name = catalog.getName();
            this.attributes = catalog.getAttributes();
            this.parentId = catalog.getParentId();
        }
    }
}
