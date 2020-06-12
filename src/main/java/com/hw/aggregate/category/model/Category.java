package com.hw.aggregate.category.model;

import com.hw.aggregate.category.CategoryRepo;
import com.hw.aggregate.category.command.CreateCategoryCommand;
import com.hw.aggregate.category.command.UpdateCategoryCommand;
import com.hw.aggregate.category.exception.CategoryNotFoundException;
import com.hw.shared.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Entity
@Table(name = "Category")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Category extends Auditable {

    @Id
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    public static Category create(Long id, CreateCategoryCommand command, CategoryRepo repo) {
        return repo.save(new Category(id, command));
    }

    public static Category get(Long id, CategoryRepo repo) {
        Optional<Category> findById = repo.findById(id);
        if (findById.isEmpty())
            throw new CategoryNotFoundException();
        return findById.get();
    }

    public static void update(Long id, UpdateCategoryCommand command, CategoryRepo categoryRepo) {
        Category category = get(id, categoryRepo);
        category.setTitle(command.getTitle());
        categoryRepo.save(category);
    }

    public static void delete(Long id, CategoryRepo categoryRepo) {
        Category category = get(id, categoryRepo);
        categoryRepo.delete(category);
    }

    private Category(Long id, CreateCategoryCommand command) {
        this.id = id;
        this.title = command.getTitle();
    }
}
