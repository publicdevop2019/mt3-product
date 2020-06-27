package com.hw.aggregate.product;

import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.representation.ProductAdminGetAllPaginatedSummaryRepresentation;
import com.hw.aggregate.product.representation.ProductCustomerSearchByAttributesSummaryPaginatedRepresentation;
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
    public ResponseEntity<ProductCustomerSearchByAttributesSummaryPaginatedRepresentation> searchProductsByAttributes(
            @RequestParam(name = "attributes") String attributes, @RequestParam("pageNum") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize, @RequestParam("sortBy") String sortBy,
            @RequestParam("sortOrder") String sortOrder) {
        return ResponseEntity.ok(productService.searchByAttributesForCustomer(attributes, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("admin/productDetails")
    public ResponseEntity<ProductAdminGetAllPaginatedSummaryRepresentation> getProducts(
            @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        ProductAdminGetAllPaginatedSummaryRepresentation allForAdmin = productService.getAllForAdmin(pageNumber, pageSize);
        return ResponseEntity.ok(allForAdmin);
    }

    @GetMapping("admin/productDetails/search")
    public ResponseEntity<?> getProducts(@RequestParam(name = "attributes") String attributes, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.searchByAttributesForAdmin(attributes, pageNumber, pageSize));
    }

    @GetMapping("public/productDetails/search")
    public ResponseEntity<?> searchProductByName(@RequestParam("key") String key, @RequestParam("pageNum") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productService.searchProductByNameForCustomer(key, pageNumber, pageSize));
    }

    @PostMapping("internal/productDetails/validate")
    public ResponseEntity<?> validateProduct(@RequestBody List<ProductValidationCommand> products) {
        return ResponseEntity.ok(productService.validateProduct(products).getResult());
    }

    @GetMapping("public/productDetails/{id}")
    public ResponseEntity<?> getProductByIdForCustomer(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.getProductByIdForCustomer(id));
    }

    @GetMapping("admin/productDetails/{id}")
    public ResponseEntity<?> getProductByIdForAdmin(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.getProductByIdForAdmin(id));
    }


    @PostMapping("admin/productDetails")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductAdminCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail).getId()).build();
    }


    @PutMapping("admin/productDetails/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "id") Long id, @RequestBody UpdateProductAdminCommand newProductDetail) {
        productService.updateProduct(id, newProductDetail);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/productDetails/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.delete(id);
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
    public ResponseEntity<?> increaseOrderStorage(@RequestBody IncreaseOrderStorageCommand command) {
        productService.increaseOrderStorageForMappedProducts(command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("internal/productDetails/revoke")
    public ResponseEntity<?> revoke(@RequestParam(name = "optToken") String txId) {
        productService.revoke(new RevokeRecordedChangeCommand(txId));
        return ResponseEntity.ok().build();
    }
}
