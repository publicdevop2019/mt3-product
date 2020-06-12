package com.hw.aggregate.category.representation;

import com.hw.aggregate.category.model.Category;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategorySummaryAdminRepresentation {

    private List<CategorySummaryCardRepresentation> categoryList;

    public CategorySummaryAdminRepresentation(List<Category> categoryList) {
        this.categoryList = categoryList.stream().map(CategorySummaryAdminRepresentation.CategorySummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CategorySummaryCardRepresentation {
        private Long id;
        private String title;

        public CategorySummaryCardRepresentation(Category category) {
            this.id = category.getId();
            this.title = category.getTitle();
        }
    }
}
