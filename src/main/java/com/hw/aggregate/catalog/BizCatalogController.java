package com.hw.aggregate.catalog;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.catalog.command.AdminCreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.AdminUpdateBizCatalogCommand;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.validation.BizValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "catalogs")
public class BizCatalogController {

    @Autowired
    private PublicBizCatalogApplicationService catalogPublicApplicationService;

    @Autowired
    private AdminBizCatalogApplicationService catalogAdminApplicationService;
    @Autowired
    BizValidator validator;
    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogPublicApplicationService.readByQuery(null, pageParam, skipCount));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogAdminApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody AdminCreateBizCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminCreateCatalogCommand",command);
        return ResponseEntity.ok().header("Location", catalogAdminApplicationService.create(command, changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody AdminUpdateBizCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminUpdateCatalogCommand",command);
        catalogAdminApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(catalogAdminApplicationService.readById(id));
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.deleteById(id,changeId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.deleteByQuery(queryParam,changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        catalogAdminApplicationService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }
}
