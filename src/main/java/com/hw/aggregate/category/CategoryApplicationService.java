package com.hw.aggregate.category;

import com.hw.aggregate.category.command.CreateCategoryCommand;
import com.hw.aggregate.category.command.UpdateCategoryCommand;
import com.hw.aggregate.category.model.Category;
import com.hw.aggregate.category.representation.CategoryRepresentation;
import com.hw.aggregate.category.representation.CategorySummaryCustomerRepresentation;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryApplicationService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private IdGenerator idGenerator;

    public CategorySummaryCustomerRepresentation getAll() {
        return new CategorySummaryCustomerRepresentation(categoryRepo.findAll());
    }

    public CategoryRepresentation create(CreateCategoryCommand command) {
        return new CategoryRepresentation(Category.create(idGenerator.getId(), command, categoryRepo));
    }

    public void update(Long id, UpdateCategoryCommand command) {
        Category.update(id, command, categoryRepo);
    }

    public void delete(Long id) {
        Category.delete(id, categoryRepo);
    }

}
