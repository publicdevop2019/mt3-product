package com.hw.service;

import com.hw.clazz.CategoryException;
import com.hw.entity.Category;
import com.hw.repo.CategoryRepo;
import com.hw.shared.IdGenerator;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private IdGenerator idGenerator;

    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    public String create(Category category) {
        category.setId(idGenerator.getId());
        return categoryRepo.save(category).getId().toString();
    }

    public void update(Long categoryId, Category newCategory) {
        Category old = getById.apply(categoryId);
        newCategory.setId(categoryId);
        BeanUtils.copyProperties(newCategory, old);
        categoryRepo.save(old);
    }

    public void delete(Long categoryId) {
        categoryRepo.delete(getById.apply(categoryId));
    }

    protected ThrowingFunction<Long, Category, CategoryException> getById = (categoryId) -> {
        Optional<Category> findById = categoryRepo.findById(categoryId);
        if (findById.isEmpty())
            throw new CategoryException("categoryId not found :: " + categoryId);
        return findById.get();
    };
}
