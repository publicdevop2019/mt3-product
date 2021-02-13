package com.mt.mall.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.validate.BizValidator;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.product.ProductApplicationService;
import com.mt.mall.application.product.command.CreateProductCommand;
import com.mt.mall.application.product.command.UpdateProductCommand;
import com.mt.mall.application.product.representation.*;
import com.mt.mall.domain.model.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "products")
public class ProductResource {

    @Autowired
    BizValidator validator;

    @GetMapping("public")
    public ResponseEntity<?> readForPublicByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Product> products = productApplicationService().products(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(products, PublicProductCardRepresentation::new));
    }

    @GetMapping("public/{id}")
    public ResponseEntity<?> readForPublicById(@PathVariable(name = "id") String id) {
        Optional<Product> product = productApplicationService().product(id);
        return product.map(value -> ResponseEntity.ok(new PublicProductRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipFlag) {
        SumPagedRep<Product> products = productApplicationService().products(queryParam, pageParam, skipFlag);
        return ResponseEntity.ok(new SumPagedRep(products, ProductCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable(name = "id") String id) {
        Optional<Product> product = productApplicationService().product(id);
        return product.map(value -> ResponseEntity.ok(new ProductRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @PostMapping("admin")
    public ResponseEntity<?> createForAdmin(@RequestBody CreateProductCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminCreateProductCommand", command);
        return ResponseEntity.ok().header("Location", productApplicationService().create(command, changeId)).build();
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> replaceForAdminById(@PathVariable(name = "id") String id, @RequestBody UpdateProductCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminUpdateProductCommand", command);
        command.setChangeId(changeId);
        productApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") String id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productApplicationService().patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable(name = "id") String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productApplicationService().removeById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin")
    public ResponseEntity<?> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productApplicationService().removeByQuery(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("app")
    public ResponseEntity<?> patchForApp(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        productApplicationService().patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<?> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Product> products = productApplicationService().products(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(products, InternalProductRepresentation::new));
    }


    @DeleteMapping("change/app/{id}")
    public ResponseEntity<?> rollbackChange(@PathVariable(name = "id") String id) {
        productApplicationService().rollback(id);
        return ResponseEntity.ok().build();
    }

    private ProductApplicationService productApplicationService() {
        return ApplicationServiceRegistry.productApplicationService();
    }

}
