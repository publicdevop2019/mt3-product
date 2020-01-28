package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.entity.SnapshotProduct;
import com.hw.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class ProductController {

    @Autowired
    ProductService productService;

    /**
     * public access
     */

    @GetMapping("categories/{categoryName}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryName") String categoryName) {
        return ResponseEntity.ok(productService.getByCategory.apply(categoryName));
    }

    @GetMapping("categories/all")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * public access
     */
    @GetMapping("productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key) {
        return ResponseEntity.ok(productService.search(key));
    }

    @PostMapping("productDetails/validate")
    public ResponseEntity<?> validateProductDetails(@RequestBody List<SnapshotProduct> products) {
        Boolean containInvalidValue = productService.validate(products);
        Map<String, String> result = new HashMap<>();
        if (containInvalidValue) {
            result.put("result", "false");
        } else {
            result.put("result", "true");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * public access
     */
    @GetMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> getProductById(@PathVariable(name = "productDetailId") Long productDetailId) {
        return ResponseEntity.ok(productService.getById.apply(productDetailId));
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestBody ProductDetail productDetail) {
        return ResponseEntity.ok().header("Location", productService.create(productDetail)).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        productService.getById.andThen(productService.update).accept(productDetailId, newProductDetail);
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody Map<String, String> productMap) {
        productService.decreaseOrderStorage.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> productMap) {
        productService.decreaseActualStorage.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> productMap) {
        productService.increaseOrderStorage.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        productService.delete(productDetailId);
        return ResponseEntity.ok().build();
    }

}
