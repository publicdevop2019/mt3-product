package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
public class BizFilterController {

    @Autowired
    private BizFilterApplicationService bizFilterApplicationService;

    @GetMapping("admin/filters")
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok(bizFilterApplicationService.getAll());
    }

    @GetMapping("public/filters/search")
    public ResponseEntity<?> getFilter(@RequestParam("catalogId") String catalog) {
        return ResponseEntity.ok(bizFilterApplicationService.getByCatalog(catalog));
    }

    @GetMapping("admin/filters/{filterId}")
    public ResponseEntity<?> getById(@PathVariable(name = "filterId") Long filterId) {
        return ResponseEntity.ok(bizFilterApplicationService.getById(filterId));
    }

    @PostMapping("admin/filters")
    public ResponseEntity<?> create(@RequestBody CreateBizFilterCommand command) {
        return ResponseEntity.ok().header("Location", bizFilterApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/filters/{filterId}")
    public ResponseEntity<?> update(@PathVariable(name = "filterId") Long filterId, @RequestBody UpdateBizFilterCommand command) {
        bizFilterApplicationService.update(filterId, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/filters/{filterId}")
    public ResponseEntity<?> delete(@PathVariable(name = "filterId") Long filterId) {
        bizFilterApplicationService.delete(filterId);
        return ResponseEntity.ok().build();
    }
}
