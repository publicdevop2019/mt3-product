package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminQueryConfig;
import com.hw.shared.SortOrder;
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
    public ResponseEntity<?> getAll(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) AdminQueryConfig.SortBy sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(attributeApplicationService.getAllAttributes(pageNumber, pageSize, sortBy, sortOrder));
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
