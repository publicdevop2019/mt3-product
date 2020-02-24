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
    private ProductService productService;

    /**
     * public access
     */

    @GetMapping("categories/{categoryName}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryName") String categoryName, @RequestParam("pageNum") Integer pageNumber,
                                                   @RequestParam("pageSize") Integer pageSize, @RequestParam("sortBy") String sortBy, @RequestParam("sortOrder") String sortOrder) {
        return ResponseEntity.ok(productService.getByCategory(categoryName, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("categories/all")
    public ResponseEntity<?> getAllProducts(@RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.getAll(pageNumber, pageSize));
    }

    /**
     * public access
     */
    @GetMapping("productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.search(key, pageNumber, pageSize));
    }

    @PostMapping("productDetails/validate")
    public ResponseEntity<?> validateProduct(@RequestBody List<SnapshotProduct> products) {
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
        productService.decreaseOrderStorageForMappedProducts.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> productMap) {
        productService.decreaseActualStorageForMappedProducts.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> productMap) {
        productService.increaseOrderStorageForMappedProducts.accept(productMap);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        productService.delete(productDetailId);
        return ResponseEntity.ok().build();
    }

}
