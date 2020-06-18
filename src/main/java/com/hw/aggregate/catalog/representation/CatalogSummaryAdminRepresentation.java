package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CatalogSummaryAdminRepresentation {

    private List<CategorySummaryCardRepresentation> categoryList;

    public CatalogSummaryAdminRepresentation(List<Catalog> categoryList) {
        this.categoryList = categoryList.stream().map(CatalogSummaryAdminRepresentation.CategorySummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CategorySummaryCardRepresentation {
        private Long id;
        private String title;

        public CategorySummaryCardRepresentation(Catalog category) {
            this.id = category.getId();
            this.title = category.getTitle();
        }
    }
}
