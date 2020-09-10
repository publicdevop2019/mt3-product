package com.hw.aggregate.product;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.shared.sql.PatchCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<?> createForAdmin(@RequestBody AdminCreateProductCommand productDetail, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", adminProductApplicationService.create(productDetail, changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody AdminUpdateProductCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        command.setChangeId(changeId);
        adminProductApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        adminProductApplicationService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminProductApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminProductApplicationService.deleteById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminProductApplicationService.deleteByQuery(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        appProductApplicationService.patchBatch(patch, changeId);
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
        appProductApplicationService.rollback(id);
        return ResponseEntity.ok().build();
    }


}
