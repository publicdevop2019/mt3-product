package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "attributes")
public class BizAttributeController {
    @Autowired
    private BizAttributeApplicationService attributeApplicationService;

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(attributeApplicationService.readForAdminByQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(attributeApplicationService.readForAdminById(id));
    }

    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateBizAttributeCommand command) {
        return ResponseEntity.ok().header("Location", attributeApplicationService.createForAdmin(command).getId().toString()).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") Long id, @RequestBody UpdateBizAttributeCommand command) {
        attributeApplicationService.replaceForAdminById(id, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") Long id) {
        attributeApplicationService.deleteForAdminById(id);
        return ResponseEntity.ok().build();
    }
}
