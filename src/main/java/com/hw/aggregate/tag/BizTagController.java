package com.hw.aggregate.tag;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.tag.command.CreateBizTagCommand;
import com.hw.aggregate.tag.command.UpdateBizTagCommand;
import com.hw.shared.sql.PatchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "attributes")
public class BizTagController {
    @Autowired
    private AdminBizTagApplicationService attributeApplicationService;

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(attributeApplicationService.readByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(attributeApplicationService.readById(id));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateBizTagCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", attributeApplicationService.create(command,changeId).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody UpdateBizTagCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        attributeApplicationService.replaceById(id, command,changeId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        attributeApplicationService.deleteById(id,changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        attributeApplicationService.deleteByQuery(queryParam,changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        attributeApplicationService.patchById(id, patch,params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        attributeApplicationService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }
}
