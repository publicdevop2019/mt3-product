package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminQueryConfig;
import com.hw.shared.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json")
public class BizFilterController {

    @Autowired
    private BizFilterApplicationService bizFilterApplicationService;

    @GetMapping("admin/filters")
    public ResponseEntity<?> getList(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) AdminQueryConfig.SortBy sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(bizFilterApplicationService.getAll(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("public/filters/search")
    public ResponseEntity<?> searchFilter(@RequestParam("catalogId") String catalog) {
        return ResponseEntity.ok(bizFilterApplicationService.getByCatalog(catalog));
    }

    @GetMapping("admin/filters/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(bizFilterApplicationService.getById(id));
    }

    @PostMapping("admin/filters")
    public ResponseEntity<?> create(@RequestBody CreateBizFilterCommand command) {
        return ResponseEntity.ok().header("Location", bizFilterApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/filters/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody UpdateBizFilterCommand command) {
        bizFilterApplicationService.update(id, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/filters/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        bizFilterApplicationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
