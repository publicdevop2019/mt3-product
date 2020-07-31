package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json")
public class BizAttributeController {
    @Autowired
    private BizAttributeApplicationService attributeApplicationService;

    @GetMapping("admin/attributes")
    public ResponseEntity<?> adminQuery(
            @RequestParam(name = HTTP_PARAM_SEARCH, required = false) String queryParam,
            @RequestParam(name = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(name = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        return ResponseEntity.ok(attributeApplicationService.adminQuery(queryParam, pageParam, skipCount));
    }

    @GetMapping("admin/attributes/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(attributeApplicationService.getById(id));
    }

    @PostMapping("admin/attributes")
    public ResponseEntity<?> create(@RequestBody CreateBizAttributeCommand command) {
        return ResponseEntity.ok().header("Location", attributeApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/attributes/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody UpdateBizAttributeCommand command) {
        attributeApplicationService.update(id, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/attributes/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        attributeApplicationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
