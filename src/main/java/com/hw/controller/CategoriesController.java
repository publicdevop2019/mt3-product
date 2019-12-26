package com.hw.controller;

import com.hw.entity.Category;
import com.hw.repo.CategoryRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class CategoriesController {
    @Autowired
    CategoryRepo categoryRepo;

    @GetMapping("categories")
    public ResponseEntity<?> getCategoryList() {
        List<Category> categoryList = categoryRepo.findAll();
        if (categoryList.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoryList);
    }


    @PostMapping("categories")
    public ResponseEntity<?> createProduct(@RequestHeader("authorization") String authorization, @RequestBody Category category) {
        Category save = categoryRepo.save(category);
        return ResponseEntity.ok().header("Location", save.getId().toString()).build();
    }


    @PutMapping("categories/{categoryId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "categoryId") Long CategoryId, @RequestBody Category newCategory) {
        Optional<Category> findById = categoryRepo.findById(CategoryId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        Category old = findById.get();
        BeanUtils.copyProperties(newCategory, old);
        categoryRepo.save(old);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("categories{categoryId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "categoryId") Long CategoryId) {
        Optional<Category> findById = categoryRepo.findById(CategoryId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        categoryRepo.delete(findById.get());
        return ResponseEntity.ok().build();
    }
}
