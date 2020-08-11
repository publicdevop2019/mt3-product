package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "filters")
public class BizFilterController {

    @Autowired
    private BizFilterApplicationService bizFilterApplicationService;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                  @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                  @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(bizFilterApplicationService.readForPublicByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(bizFilterApplicationService.readForAdminByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(bizFilterApplicationService.readForAdminById(id));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateBizFilterCommand command) {
        return ResponseEntity.ok().header("Location", bizFilterApplicationService.createForAdmin(command).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdmin(@PathVariable(name = "id") Long id, @RequestBody UpdateBizFilterCommand command) {
        bizFilterApplicationService.replaceForAdmin(id, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        bizFilterApplicationService.deleteForAdminById(id);
        return ResponseEntity.ok().build();
    }
}
