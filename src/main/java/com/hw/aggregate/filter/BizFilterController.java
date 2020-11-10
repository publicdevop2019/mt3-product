package com.hw.aggregate.filter;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.shared.validation.BizValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "filters")
public class BizFilterController {

    @Autowired
    private AdminBizFilterApplicationService bizFilterApplicationService;
    @Autowired
    private PublicBizFilterApplicationService publicBizFilterApplicationService;
    @Autowired
    BizValidator validator;
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
        validator.validate("adminCreateFilterCommand",command);
        return ResponseEntity.ok().header("Location", bizFilterApplicationService.create(command, changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdmin(@PathVariable(name = "id") Long id, @RequestBody UpdateBizFilterCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminUpdateFilterCommand",command);
        bizFilterApplicationService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        bizFilterApplicationService.deleteById(id,changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        bizFilterApplicationService.deleteByQuery(queryParam,changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        bizFilterApplicationService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }
}
