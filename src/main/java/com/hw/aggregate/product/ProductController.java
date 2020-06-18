package com.hw.aggregate.product;

import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.command.ProductValidationCommand;
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

    /**
     * public access
     */
    @GetMapping("catalogs/{catalogName}")
    public ResponseEntity<?> getProductsByCatalog(@PathVariable(name = "catalogName") String catalogName, @RequestParam("pageNum") Integer pageNumber,
                                                  @RequestParam("pageSize") Integer pageSize, @RequestParam("sortBy") String sortBy,
                                                  @RequestParam("sortOrder") String sortOrder) {
        return ResponseEntity.ok(productService.getByCatalog(catalogName, pageNumber, pageSize, sortBy, sortOrder).getProductSimpleList());
    }

    @GetMapping("catalogs/all")
    public ResponseEntity<?> getAllProducts(@RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.getAll(pageNumber, pageSize));
    }

    /**
     * public access
     */
    @GetMapping("productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.searchProduct(key, pageNumber, pageSize).getProductSearchRepresentations());
    }

    @PostMapping("productDetails/validate")
    public ResponseEntity<?> validateProduct(@RequestBody List<ProductValidationCommand> products) {
        return ResponseEntity.ok(productService.validateProduct(products).getResult());
    }

    /**
     * public access
     */
    @GetMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> getProductByIdForCustomer(@PathVariable(name = "productDetailId") Long productDetailId) {
        return ResponseEntity.ok(productService.getProductByIdForCustomer(productDetailId));
    }

    @GetMapping("productDetails/admin/{productDetailId}")
    public ResponseEntity<?> getProductByIdForAdmin(@PathVariable(name = "productDetailId") Long productDetailId) {
        return ResponseEntity.ok(productService.getProductByIdForAdmin(productDetailId));
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductAdminCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail).getId()).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productDetailId") Long productDetailId, @RequestBody UpdateProductAdminCommand newProductDetail) {
        productService.updateProduct(productDetailId, newProductDetail);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) {
        productService.delete(new DeleteProductAdminCommand(productDetailId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.decreaseOrderStorageForMappedProducts(new DecreaseOrderStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.decreaseActualStorageForMappedProducts(new DecreaseActualStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }


    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody Map<String, String> productMap, @RequestParam(name = "optToken") String txId) {
        productService.increaseOrderStorageForMappedProducts(new IncreaseOrderStorageCommand(productMap, txId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("productDetails/revoke")
    public ResponseEntity<?> revoke(@RequestParam(name = "optToken") String txId) {
        productService.revoke(new RevokeRecordedChangeCommand(txId));
        return ResponseEntity.ok().build();
    }
}
