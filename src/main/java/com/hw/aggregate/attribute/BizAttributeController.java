package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
public class BizAttributeController {
    @Autowired
    private BizAttributeApplicationService attributeApplicationService;

    @GetMapping("admin/attributes")
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok(attributeApplicationService.getAllAttributes());
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
