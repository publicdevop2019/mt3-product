package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
public class CatalogController {

    @Autowired
    private CatalogApplicationService categoryService;

    @GetMapping("categories")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAllForCustomer());
    }

    @GetMapping("admin/categories")
    public ResponseEntity<?> getCategoryAdminList() {
        return ResponseEntity.ok(categoryService.getAllForAdmin());
    }


    @PostMapping("categories")
    public ResponseEntity<?> createCategory(@RequestBody CreateCatalogCommand command) {
        return ResponseEntity.ok().header("Location", categoryService.create(command).getId().toString()).build();
    }


    @PutMapping("categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable(name = "categoryId") Long categoryId, @RequestBody UpdateCatalogCommand command) {
        categoryService.update(categoryId, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().build();
    }
}
