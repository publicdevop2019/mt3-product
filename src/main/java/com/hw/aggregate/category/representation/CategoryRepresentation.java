package com.hw.aggregate.category.representation;

import com.hw.aggregate.category.model.Category;
import lombok.Data;

@Data
public class CategoryRepresentation {
    private Long id;
    private String title;

    public CategoryRepresentation(Category category) {
        id = category.getId();
        title = category.getTitle();
    }
}
