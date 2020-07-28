package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminSortConfig;
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
    public ResponseEntity<?> getList(
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_NUM_NAME, required = false) Integer pageNumber,
            @RequestParam(value = HTTP_PARAM_SORT_PAGE_SIZE_NAME, required = false) Integer pageSize,
            @RequestParam(value = HTTP_PARAM_SORT_BY_NAME, required = false) AdminSortConfig sortBy,
            @RequestParam(value = HTTP_PARAM_SORT_ORDER_NAME, required = false) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(attributeApplicationService.getAllAttributes(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("admin/attributes")
    public ResponseEntity<?> create(@RequestBody CreateBizAttributeCommand command) {
        return ResponseEntity.ok().header("Location", attributeApplicationService.create(command).getId().toString()).build();
    }


    @PutMapping("admin/attributes/{attributeId}")
    public ResponseEntity<?> update(@PathVariable(name = "attributeId") Long attributeId, @RequestBody UpdateBizAttributeCommand command) {
        attributeApplicationService.update(attributeId, command);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("admin/attributes/{attributeId}")
    public ResponseEntity<?> delete(@PathVariable(name = "attributeId") Long attributeId) {
        attributeApplicationService.delete(attributeId);
        return ResponseEntity.ok().build();
    }
}
