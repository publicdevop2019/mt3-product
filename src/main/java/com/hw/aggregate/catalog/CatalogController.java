package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
public class CatalogController {

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @GetMapping("catalogs")
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok(catalogApplicationService.getAllForCustomer());
    }

    @GetMapping("admin/catalogs")
    public ResponseEntity<?> getAdminList() {
        return ResponseEntity.ok(catalogApplicationService.getAllForAdmin());
    }


    @PostMapping("catalogs")
    public ResponseEntity<?> create(@RequestBody CreateCatalogCommand command) {
        return ResponseEntity.ok().header("Location", catalogApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("catalogs/{catalogId}")
    public ResponseEntity<?> update(@PathVariable(name = "catalogId") Long catalogId, @RequestBody UpdateCatalogCommand command) {
        catalogApplicationService.update(catalogId, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("catalogs/{catalogId}")
    public ResponseEntity<?> delete(@PathVariable(name = "catalogId") Long catalogId) {
        catalogApplicationService.delete(catalogId);
        return ResponseEntity.ok().build();
    }
}
