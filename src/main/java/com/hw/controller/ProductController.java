package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.entity.SnapshotProduct;
import com.hw.service.ProductService;
import com.hw.shared.ThrowingBiConsumer;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
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
        return respExceptionHelper(productService.getProductsByCategory, categoryName);
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
        return respExceptionHelper(productService.getProductById, productDetailId);
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestBody ProductDetail productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail)).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        return respExceptionHelper(productService.updateProduct, productDetailId, newProductDetail);
    }

    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody Map<String, String> productMap) {
        return respExceptionHelper(productService.decreaseOrderStorage, productMap);
    }

    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> productMap) {
        return respExceptionHelper(productService.decreaseActualStorage, productMap);
    }

    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> productMap) {
        return respExceptionHelper(productService.increaseOrderStorage, productMap);
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        return respExceptionHelper(productService.deleteProduct, productDetailId);
    }

    private ResponseEntity<?> respExceptionHelper(ThrowingConsumer<Object, Exception> throwingConsumer, Object input) {
        try {
            throwingConsumer.accept(input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> respExceptionHelper(ThrowingFunction<Object, Object, Exception> throwingFunction, Object input) {
        try {
            return ResponseEntity.ok(throwingFunction.accept(input));
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> respExceptionHelper(ThrowingBiConsumer<Object, Object, Exception> throwingFunction, Object input1, Object input2) {
        try {
            throwingFunction.accept(input1, input2);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error during method call -->", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
