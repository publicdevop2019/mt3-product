package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.entity.SnapshotProduct;
import com.hw.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        try {
            List<ProductSimple> productsByCategory = productService.getProductsByCategory(categoryName);
            return ResponseEntity.ok(productsByCategory);
        } catch (Exception e) {
            log.warn("warning during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("categories/all")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * public access
     */
    @GetMapping("productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key) {
        return ResponseEntity.ok(productService.searchProduct(key));
    }

    @PostMapping("productDetails/validate")
    public ResponseEntity<?> validateProductDetails(@RequestBody List<SnapshotProduct> products) {
        Boolean containInvalidValue = productService.validateProductDetails(products);
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
        try {
            return ResponseEntity.ok(productService.getProductById(productDetailId));
        } catch (Exception e) {
            log.warn("warning during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestBody ProductDetail productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail)).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        try {
            productService.updateProduct(productDetailId, newProductDetail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.decreaseOrderStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.decreaseActualStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.increaseOrderStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        try {
            productService.deleteProduct(productDetailId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
