package com.hw.aggregate.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.representation.AdminProductSumPagedRep;
import com.hw.aggregate.product.representation.AppProductSumPagedRep;
import com.hw.aggregate.product.representation.PublicProductSumPagedRep;
import com.hw.shared.PatchCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hw.shared.AppConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "products")
public class ProductController {

    @Autowired
    private ProductApplicationService productService;

    @GetMapping("public")
    public ResponseEntity<PublicProductSumPagedRep> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(productService.readForPublicByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("public/{id}")
    public ResponseEntity<?> readForPublicById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.readForPublicById(id));
    }

    @GetMapping("admin")
    public ResponseEntity<AdminProductSumPagedRep> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipFlag) {
        return ResponseEntity.ok(productService.readForAdminByQuery(queryParam, pageParam, skipFlag));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.readForAdminById(id));
    }


    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody AdminCreateProductCommand productDetail) {
        return ResponseEntity.ok().header("Location", productService.createForAdmin(productDetail).getId()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody AdminUpdateProductCommand newProductDetail) {
        productService.replaceForAdminById(id, newProductDetail);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(productService.patchForAdminById(id, patch));
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdmin(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productService.patchForAdmin(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        productService.deleteForAdminById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        productService.deleteForAdminByQuery(queryParam);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productService.patchForApp(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<AppProductSumPagedRep> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(productService.readForAppByQuery(queryParam, pageParam, skipCount));
    }


    @DeleteMapping("change/app/{id}")
    public ResponseEntity<?> rollbackChange(@PathVariable(name = "id") String id) {
        productService.rollbackChangeForApp(id);
        return ResponseEntity.ok().build();
    }


}
