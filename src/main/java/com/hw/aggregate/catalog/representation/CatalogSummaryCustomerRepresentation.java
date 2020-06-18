package com.hw.aggregate.catalog.representation;

import com.hw.aggregate.catalog.model.Catalog;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CatalogSummaryCustomerRepresentation {

    private List<CategorySummaryCardRepresentation> categoryList;

    public CatalogSummaryCustomerRepresentation(List<Catalog> categoryList) {
        this.categoryList = categoryList.stream().map(CatalogSummaryCustomerRepresentation.CategorySummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CategorySummaryCardRepresentation {
        private String title;

        public CategorySummaryCardRepresentation(Catalog category) {
            this.title = category.getTitle();
        }
    }
}
