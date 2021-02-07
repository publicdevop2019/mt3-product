package com.mt.mall.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.validate.BizValidator;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.catalog.CatalogApplicationService;
import com.mt.mall.application.catalog.command.CreateCatalogCommand;
import com.mt.mall.application.catalog.command.UpdateCatalogCommand;
import com.mt.mall.application.catalog.representation.CatalogRepresentation;
import com.mt.mall.domain.model.catalog.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "catalogs")
public class CatalogResource {

    @Autowired
    BizValidator validator;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogApplicationService().publicCatalogs(pageParam, skipCount));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogApplicationService().catalogs(queryParam, pageParam, skipCount));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminCreateCatalogCommand", command);
        return ResponseEntity.ok().header("Location", catalogApplicationService().create(command, changeId)).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") String id, @RequestBody UpdateCatalogCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminUpdateCatalogCommand", command);
        catalogApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Catalog> catalog = catalogApplicationService().catalog(id);
        return catalog.map(value -> ResponseEntity.ok(new CatalogRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().removeCatalog(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().removeCatalogs(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") String id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        catalogApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    private CatalogApplicationService catalogApplicationService() {
        return ApplicationServiceRegistry.catalogApplicationService();
    }
}
