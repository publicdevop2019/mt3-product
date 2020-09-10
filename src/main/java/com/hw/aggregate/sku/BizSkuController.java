package com.hw.aggregate.sku;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.sku.command.AdminCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AdminUpdateBizSkuCommand;
import com.hw.aggregate.sku.model.AdminBizSkuUpdateQueryBuilder;
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
@RequestMapping(produces = "application/json", path = "sku")
public class BizSkuController {
    @Autowired
    private AdminBizSkuApplicationService adminBizSkuApplicationService;
    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(adminBizSkuApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(adminBizSkuApplicationService.readById(id));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody AdminCreateBizSkuCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", adminBizSkuApplicationService.create(command, changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody AdminUpdateBizSkuCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminBizSkuApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        adminBizSkuApplicationService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminBizSkuApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminBizSkuApplicationService.deleteById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminBizSkuApplicationService.deleteByQuery(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        appBizSkuApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<?> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(appBizSkuApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }
}
