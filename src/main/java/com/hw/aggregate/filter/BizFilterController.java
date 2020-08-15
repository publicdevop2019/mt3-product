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
    private AdminBizFilterApplicationService bizFilterApplicationService;
    @Autowired
    private PublicBizFilterApplicationService publicBizFilterApplicationService;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                  @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                  @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(publicBizFilterApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(bizFilterApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(bizFilterApplicationService.readById(id));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateBizFilterCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", bizFilterApplicationService.create(command,changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdmin(@PathVariable(name = "id") Long id, @RequestBody UpdateBizFilterCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        bizFilterApplicationService.replaceById(id, command,changeId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        bizFilterApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
