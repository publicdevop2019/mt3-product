package com.hw.aggregate.catalog;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.catalog.command.CreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateBizCatalogCommand;
import com.hw.shared.sql.PatchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "catalogs")
public class BizCatalogController {

    @Autowired
    private PublicBizCatalogApplicationService catalogPublicApplicationService;

    @Autowired
    private AdminBizCatalogApplicationService catalogAdminApplicationService;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogPublicApplicationService.readByQuery(queryParam, pageParam, skipCount));
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
    public ResponseEntity<?> createForAdmin(@RequestBody CreateBizCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", catalogAdminApplicationService.create(command, changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody UpdateBizCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(catalogAdminApplicationService.readById(id));
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        catalogAdminApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.patchById(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogAdminApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }
}
