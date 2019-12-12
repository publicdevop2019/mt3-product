package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.repo.ProductRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {
    @Autowired
    ProductRepo productDetailRepo;

    /**
     * public access
     */
    @GetMapping("categories/{categoryName}")
    public ResponseEntity<?> getAllProducts(@PathVariable(name = "categoryName") String categoryName) {
        Optional<List<ProductDetail>> productByCategory = productDetailRepo.findProductByCategory(categoryName);
        if (productByCategory.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productByCategory.get());
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
        return ResponseEntity.ok().header("Location", save.getId() + "/productDetails/" + save.getId()).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        ProductDetail oldProductDetail = findById.get();
        BeanUtils.copyProperties(newProductDetail, oldProductDetail);
        productDetailRepo.save(oldProductDetail);
        return ResponseEntity.ok().build();
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
