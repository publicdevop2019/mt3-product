package com.hw.aggregate.product;

import com.hw.aggregate.product.command.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class ProductController {

    @Autowired
    private ProductApplicationService productService;

    @GetMapping("public/productDetails")
    public ResponseEntity<?> getProductsByTags(@RequestParam(name = "tags") String tags, @RequestParam("pageNum") Integer pageNumber,
                                               @RequestParam("pageSize") Integer pageSize, @RequestParam("sortBy") String sortBy,
                                               @RequestParam("sortOrder") String sortOrder) {
        return ResponseEntity.ok(productService.searchByTagsForCustomer(tags, pageNumber, pageSize, sortBy, sortOrder).getProductSimpleList());
    }

    @GetMapping("admin/productDetails")
    public ResponseEntity<?> getProducts(@RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.getAllForAdmin(pageNumber, pageSize));
    }
    @GetMapping("admin/productDetails/search")
    public ResponseEntity<?> getProducts(@RequestParam(name = "tags") String tags, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.searchByTagsForAdmin(tags,pageNumber, pageSize));
    }

    @GetMapping("public/productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.searchProductForCustomer(key, pageNumber, pageSize).getData());
    }

    @PostMapping("internal/productDetails/validate")
    public ResponseEntity<?> validateProduct(@RequestBody List<ProductValidationCommand> products) {
        return ResponseEntity.ok(productService.validateProduct(products).getResult());
    }

    @GetMapping("public/productDetails/{productDetailId}")
    public ResponseEntity<?> getProductByIdForCustomer(@PathVariable(name = "productDetailId") Long productDetailId) {
        return ResponseEntity.ok(productService.getProductByIdForCustomer(productDetailId));
    }

    @GetMapping("admin/productDetails/{productDetailId}")
    public ResponseEntity<?> getProductByIdForAdmin(@PathVariable(name = "productDetailId") Long productDetailId) {
        return ResponseEntity.ok(productService.getProductByIdForAdmin(productDetailId));
    }


    @PostMapping("admin/productDetails")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductAdminCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail).getId()).build();
    }


    @PutMapping("admin/productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productDetailId") Long productDetailId, @RequestBody UpdateProductAdminCommand newProductDetail) {
        productService.updateProduct(productDetailId, newProductDetail);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        productService.delete(new DeleteProductAdminCommand(productDetailId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("shared/productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.decreaseOrderStorageForMappedProducts(new DecreaseOrderStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }


    @PutMapping("internal/productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.decreaseActualStorageForMappedProducts(new DecreaseActualStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }


    @PutMapping("shared/productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.increaseOrderStorageForMappedProducts(new IncreaseOrderStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("internal/productDetails/revoke")
    public ResponseEntity<?> revoke(@RequestParam(name = "optToken") String txId) {
        productService.revoke(new RevokeRecordedChangeCommand(txId));
        return ResponseEntity.ok().build();
    }
}
