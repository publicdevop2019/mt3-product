package com.hw.aggregate.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.PatchCommand;
import com.hw.aggregate.product.representation.ProductAdminSumPagedRep;
import com.hw.aggregate.product.representation.ProductCustomerSumPagedRep;
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
    public ResponseEntity<ProductCustomerSumPagedRep> queryForCustomer(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(productService.queryForCustomer(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/productDetails")
    public ResponseEntity<ProductAdminSumPagedRep> queryForAdmin(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipFlag) {
        return ResponseEntity.ok(productService.queryForAdmin(queryParam, pageParam, skipFlag));
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
    public ResponseEntity<?> createProductForAdmin(@RequestBody CreateProductAdminCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createProduct(productDetail).getId()).build();
    }


    @PutMapping("admin/productDetails/{id}")
    public ResponseEntity<?> updateProductForAdmin(@PathVariable(name = "id") Long id, @RequestBody UpdateProductAdminCommand newProductDetail) {
        productService.update(id, newProductDetail);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/productDetails/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchProduct(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(productService.patch(id, patch));
    }

    @PatchMapping("admin/productDetails")
    public ResponseEntity<?> batchUpdateProducts(@RequestBody List<PatchCommand> patch) {
        productService.update(patch);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/productDetails/{id}")
    public ResponseEntity<?> deleteProductForAdmin(@PathVariable(name = "id") Long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/productDetails")
    public ResponseEntity<?> deleteProductForAdmin(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        productService.delete(queryParam);
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

    @PutMapping("internal/transactions/rollback")
    public ResponseEntity<?> rollbackTx(@RequestParam(value = "txId") String txId) {
        productService.rollbackTx(txId);
        return ResponseEntity.ok().build();
    }
}
