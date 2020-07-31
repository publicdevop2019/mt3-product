package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
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
    public ResponseEntity<?> adminQuery(
            @RequestParam(name = HTTP_PARAM_SEARCH, required = false) String queryParam,
            @RequestParam(name = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(name = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(bizFilterApplicationService.adminQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("public/filters")
    public ResponseEntity<?> customerQuery(@RequestParam(name = HTTP_PARAM_SEARCH, required = false) String queryParam,
                                           @RequestParam(name = HTTP_PARAM_PAGE, required = false) String pageParam,
                                           @RequestParam(name = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(bizFilterApplicationService.customerQuery(queryParam, pageParam, skipCount));
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
