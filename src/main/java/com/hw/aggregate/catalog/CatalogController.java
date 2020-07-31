package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.AdminQueryConfig;
import com.hw.aggregate.catalog.model.CustomerQueryConfig;
import com.hw.shared.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json")
public class CatalogController {

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @GetMapping("public/catalogs")
    public ResponseEntity<?> getList(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) CustomerQueryConfig.SortBy sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(catalogApplicationService.getAllForCustomer(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("admin/backend/catalogs")
    public ResponseEntity<?> getAdminListBackend(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) AdminQueryConfig.SortBy sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(catalogApplicationService.getAllForAdminBackend(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("admin/frontend/catalogs")
    public ResponseEntity<?> getAdminListFrontend(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) AdminQueryConfig.SortBy sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(catalogApplicationService.getAllForAdminFrontend(pageNumber, pageSize, sortBy, sortOrder));
    }


    @PostMapping("admin/catalogs")
    public ResponseEntity<?> create(@RequestBody CreateCatalogCommand command) {
        return ResponseEntity.ok().header("Location", catalogApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/catalogs/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody UpdateCatalogCommand command) {
        catalogApplicationService.update(id, command);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/catalogs/{id}")
    public ResponseEntity<?> read(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(catalogApplicationService.read(id));
    }


    @DeleteMapping("admin/catalogs/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        catalogApplicationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
