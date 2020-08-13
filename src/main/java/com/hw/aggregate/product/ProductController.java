package com.hw.aggregate.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.shared.sql.PatchCommand;
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
    private AdminProductApplicationService adminProductApplicationService;
    @Autowired
    private AppProductApplicationService appProductApplicationService;
    @Autowired
    private PublicProductApplicationService publicProductApplicationService;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(publicProductApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("public/{id}")
    public ResponseEntity<?> readForPublicById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(publicProductApplicationService.readById(id));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipFlag) {
        return ResponseEntity.ok(adminProductApplicationService.readByQuery(queryParam, pageParam, skipFlag));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(adminProductApplicationService.readById(id));
    }


    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody AdminCreateProductCommand productDetail) {
        return ResponseEntity.ok().header("Location", adminProductApplicationService.create(productDetail).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody AdminUpdateProductCommand newProductDetail) {
        adminProductApplicationService.replaceById(id, newProductDetail);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(adminProductApplicationService.patchById(id, patch));
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdmin(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminProductApplicationService.patch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        adminProductApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam) {
        adminProductApplicationService.deleteByQuery(queryParam);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        appProductApplicationService.patchForApp(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<?> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(appProductApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }


    @DeleteMapping("change/app/{id}")
    public ResponseEntity<?> rollbackChange(@PathVariable(name = "id") String id) {
        appProductApplicationService.rollbackChangeForApp(id);
        return ResponseEntity.ok().build();
    }


}
