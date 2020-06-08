package com.hw.aggregate.category;

import com.hw.aggregate.category.command.CreateCategoryCommand;
import com.hw.aggregate.category.command.UpdateCategoryCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
public class CategoriesController {

    @Autowired
    private CategoryApplicationService categoryService;

    @GetMapping("categories")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAll());
    }


    @PostMapping("categories")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryCommand command) {
        return ResponseEntity.ok().header("Location", categoryService.create(command).getId().toString()).build();
    }


    @PutMapping("categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable(name = "categoryId") Long categoryId, @RequestBody UpdateCategoryCommand command) {
        categoryService.update(categoryId, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().build();
    }
}
