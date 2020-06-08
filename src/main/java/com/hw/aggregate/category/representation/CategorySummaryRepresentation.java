package com.hw.aggregate.category.representation;

import com.hw.aggregate.category.model.Category;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
@Data
public class CategorySummaryRepresentation {

    private List<CategorySummaryCardRepresentation> categoryList;

    public CategorySummaryRepresentation(List<Category> categoryList) {
        this.categoryList = categoryList.stream().map(CategorySummaryRepresentation.CategorySummaryCardRepresentation::new).collect(Collectors.toList());
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
