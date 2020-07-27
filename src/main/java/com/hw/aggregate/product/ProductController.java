package com.hw.aggregate.product;

import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductStatus;
import com.hw.aggregate.product.representation.ProductAdminGetAllPaginatedSummaryRepresentation;
import com.hw.aggregate.product.representation.ProductCustomerSearchByAttributesSummaryPaginatedRepresentation;
import com.hw.shared.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hw.shared.AppConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class ProductController {

    @Autowired
    private ProductApplicationService productService;

    @GetMapping("public/productDetails")
    public ResponseEntity<ProductCustomerSearchByAttributesSummaryPaginatedRepresentation> searchProductsByAttributes(
            @RequestParam(name = "attributes") String attributes,
            @RequestParam(HTTP_PARAM_SORT_PAGE_NUM_NAME) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) ProductDetail.CustomerSortConfig sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.searchByAttributesForCustomer(attributes, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("admin/productDetails")
    public ResponseEntity<ProductAdminGetAllPaginatedSummaryRepresentation> getAllProducts(
            @RequestParam(HTTP_PARAM_SORT_PAGE_NUM_NAME) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) ProductDetail.AdminSortConfig sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder) {
        ProductAdminGetAllPaginatedSummaryRepresentation allForAdmin = productService.getAllForAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(allForAdmin);
    }

    @GetMapping("admin/productDetails/search")
    public ResponseEntity<?> getProducts(
            @RequestParam(name = "attributes") String attributes,
            @RequestParam(HTTP_PARAM_SORT_PAGE_NUM_NAME) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) ProductDetail.AdminSortConfig sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.searchByAttributesForAdmin(attributes, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("public/productDetails/search")
    public ResponseEntity<?> searchProductByName(
            @RequestParam("key") String key,
            @RequestParam(HTTP_PARAM_SORT_PAGE_NUM_NAME) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) ProductDetail.CustomerSortConfig sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.searchProductByNameForCustomer(key, pageNumber, pageSize, sortBy, sortOrder));
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

    @PutMapping("admin/productDetails/{id}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") ProductStatus status) {
        productService.updateProductStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/productDetails/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("shared/productDetails/storageOrder/decrease")
    public ResponseEntity<?> decreaseOrderStorage(@RequestBody DecreaseOrderStorageCommand command) {
        productService.decreaseOrderStorageForMappedProducts(command);
        return ResponseEntity.ok().build();
    }


    @PutMapping("internal/productDetails/storageActual/decrease")
    public ResponseEntity<?> decreaseActualStorage(@RequestBody DecreaseActualStorageCommand command) {
        productService.decreaseActualStorageForMappedProducts(command);
        return ResponseEntity.ok().build();
    }


    @PutMapping("shared/productDetails/storageOrder/increase")
    public ResponseEntity<?> increaseOrderStorage(@RequestBody IncreaseOrderStorageCommand command) {
        productService.increaseOrderStorageForMappedProducts(command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("internal/productDetails/rollback")
    public ResponseEntity<?> rollbackTx(@RequestParam(name = "txId") String txId) {
        productService.rollbackTx(txId);
        return ResponseEntity.ok().build();
    }
}
