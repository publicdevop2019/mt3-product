package com.hw.controller;

import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.repo.ProductDetailRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class ProductController {

    @Autowired
    ProductDetailRepo productDetailRepo;

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
        return ResponseEntity.ok().header("Location", save.getId().toString()).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        ProductDetail oldProductSimple = findById.get();
        BeanUtils.copyProperties(newProductDetail, oldProductSimple);
        productDetailRepo.save(oldProductSimple);
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
