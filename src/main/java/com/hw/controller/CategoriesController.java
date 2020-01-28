package com.hw.controller;

import com.hw.entity.Category;
import com.hw.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class CategoriesController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("categories")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAll());
    }


    @PostMapping("categories")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok().header("Location", categoryService.create(category)).build();
    }


    @PutMapping("categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable(name = "categoryId") Long categoryId, @RequestBody Category newCategory) {
        categoryService.update(categoryId, newCategory);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().build();
    }
}
