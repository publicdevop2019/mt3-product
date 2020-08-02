package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
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
    public ResponseEntity<?> customerQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogApplicationService.customerQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/catalogs")
    public ResponseEntity<?> adminQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(catalogApplicationService.adminQuery(queryParam, pageParam, skipCount));
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
