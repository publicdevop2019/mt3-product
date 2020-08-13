package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "catalogs")
public class CatalogController {

    @Autowired
    private PublicCatalogApplicationService catalogPublicApplicationService;

    @Autowired
    private AdminCatalogApplicationService catalogAdminApplicationService;

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
    public ResponseEntity<?> createForAdmin(@RequestBody CreateCatalogCommand command) {
        return ResponseEntity.ok().header("Location", catalogAdminApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody UpdateCatalogCommand command) {
        catalogAdminApplicationService.replaceById(id, command);
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
}
