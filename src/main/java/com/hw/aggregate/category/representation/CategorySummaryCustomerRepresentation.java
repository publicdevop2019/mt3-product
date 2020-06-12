package com.hw.aggregate.category.representation;

import com.hw.aggregate.category.model.Category;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategorySummaryCustomerRepresentation {

    private List<CategorySummaryCardRepresentation> categoryList;

    public CategorySummaryCustomerRepresentation(List<Category> categoryList) {
        this.categoryList = categoryList.stream().map(CategorySummaryCustomerRepresentation.CategorySummaryCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public class CategorySummaryCardRepresentation {
        private String title;

        public CategorySummaryCardRepresentation(Category category) {
            this.title = category.getTitle();
        }
    }
}
