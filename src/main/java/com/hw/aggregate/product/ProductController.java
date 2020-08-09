package com.hw.aggregate.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.CreateProductAdminCommand;
import com.hw.aggregate.product.command.ProductValidationCommand;
import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.representation.ProductAppSumPagedRep;
import com.hw.shared.PatchCommand;
import com.hw.aggregate.product.representation.ProductAdminSumPagedRep;
import com.hw.aggregate.product.representation.ProductPublicSumPagedRep;
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
    public ResponseEntity<ProductPublicSumPagedRep> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(productService.readForPublicByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("app/productDetails")
    public ResponseEntity<ProductAppSumPagedRep> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(productService.readForAppByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/productDetails")
    public ResponseEntity<ProductAdminSumPagedRep> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipFlag) {
        return ResponseEntity.ok(productService.readForAdminByQuery(queryParam, pageParam, skipFlag));
    }

    @GetMapping("public/productDetails/{id}")
    public ResponseEntity<?> readForPublicById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.readForPublicById(id));
    }

    @GetMapping("admin/productDetails/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.readForAdminById(id));
    }


    @PostMapping("admin/productDetails")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateProductAdminCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createForAdmin(productDetail).getId()).build();
    }


    @PutMapping("admin/productDetails/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody UpdateProductAdminCommand newProductDetail) {
        productService.replaceForAdminById(id, newProductDetail);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/productDetails/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(productService.patchForAdminById(id, patch));
    }

    @PatchMapping("admin/productDetails")
    public ResponseEntity<?> patchForAdmin(@RequestBody List<PatchCommand> patch, @RequestHeader("changeId") String changeId) {
        productService.patchForAdmin(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app/productDetails")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader("changeId") String changeId) {
        productService.patchForApp(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/productDetails/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        productService.deleteForAdminById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/productDetails")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        productService.deleteForAdminByQuery(queryParam);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("app/change/{id}")
    public ResponseEntity<?> rollbackChange(@PathVariable(name = "id") String id) {
        productService.rollbackChangeForApp(id);
        return ResponseEntity.ok().build();
    }


}
