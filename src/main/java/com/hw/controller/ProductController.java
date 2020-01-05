package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.repo.ProductDetailRepo;
import com.hw.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class ProductController {

    @Autowired
    ProductDetailRepo productDetailRepo;

    @Autowired
    ProductService productService;

    /**
     * public access
     */
    @GetMapping("categories/{categoryName}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryName") String categoryName) {
        Optional<List<ProductDetail>> productByCategory = productDetailRepo.findProductByCategory(categoryName);
        if (productByCategory.isEmpty())
            return ResponseEntity.notFound().build();
        List<ProductSimple> productSimpleArrayList = new ArrayList<>();
        productByCategory.get().stream().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleArrayList.add(productSimple);
        });
        return ResponseEntity.ok(productSimpleArrayList);
    }

    @GetMapping("categories/all")
    public ResponseEntity<?> getProductsByCategory() {
        List<ProductDetail> productByCategory = productDetailRepo.findAll();
        List<ProductSimple> productSimpleArrayList = new ArrayList<>();
        productByCategory.stream().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleArrayList.add(productSimple);
        });
        return ResponseEntity.ok(productSimpleArrayList);
    }


    /**
     * public access
     */
    @GetMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> getProductById(@PathVariable(name = "productDetailId") Long productDetailId) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(findById.get());
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestHeader("authorization") String authorization, @RequestBody ProductDetail productDetail) {
        ProductDetail save = productDetailRepo.save(productDetail);
        if (productDetail.getStorage() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().header("Location", save.getId().toString()).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        if (newProductDetail.getStorage() != null)
            return ResponseEntity.badRequest().body("use increaseBy or decreaseBy to update storage value");
        ProductDetail oldProductSimple = findById.get();
        Integer storageCopied = oldProductSimple.getStorage();
        BeanUtils.copyProperties(newProductDetail, oldProductSimple);
        oldProductSimple.setStorage(storageCopied);
        if (newProductDetail.getIncreaseStorageBy() != null)
            oldProductSimple.setStorage(oldProductSimple.getStorage() + newProductDetail.getIncreaseStorageBy());
        if (newProductDetail.getDecreaseStorageBy() != null)
            oldProductSimple.setStorage(oldProductSimple.getStorage() - (newProductDetail.getDecreaseStorageBy()));
        productDetailRepo.save(oldProductSimple);
        return ResponseEntity.ok().build();
    }

    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseProductStorage(@RequestHeader("authorization") String authorization, @RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.batchDecreaseUpdate(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseProductStorage(@RequestHeader("authorization") String authorization, @RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.batchIncreaseUpdate(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        productDetailRepo.delete(findById.get());
        return ResponseEntity.ok().build();
    }
}
