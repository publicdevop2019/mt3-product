package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductQueryRegistry;
import com.hw.aggregate.product.representation.AppProductCardRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.exception.HangingTransactionException;
import com.hw.shared.idempotent.exception.RollbackNotSupportedException;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.aggregate.product.representation.AdminProductRep.ProductSkuAdminRepresentation.ADMIN_REP_ATTR_SALES_LITERAL;
import static com.hw.shared.AppConstant.*;

@Slf4j
@Service
public class AppProductApplicationService extends DefaultRoleBasedRestfulService<Product, AppProductCardRep, Void, VoidTypedClass> {

    @Autowired
    private ProductRepo repo2;
    @Autowired
    private ChangeRepository changeHistoryRepository;

    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;
    @Autowired
    private ProductQueryRegistry productQueryRegistry;

    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = productQueryRegistry;
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
        om = om2;
        changeRepository = changeHistoryRepository;
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        return null;
    }

    @Override
    public AppProductCardRep getEntitySumRepresentation(Product product) {
        return new AppProductCardRep(product, appBizSkuApplicationService);
    }

    @Override
    public Void getEntityRepresentation(Product product) {
        return null;
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(Product product) {

    }

    @Override
    public void postDelete(Product product) {

    }

    @Override
    protected void prePatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }


    @Transactional
    public Long patchForAppBatch(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeIdAndEntityType(changeId + CHANGE_REVOKED, entityClass.getName()).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId, OperationType.PATCH_BATCH, null);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer update = productQueryRegistry.update(role, noNestedEntity, entityClass);
        appBizSkuApplicationService.patchBatch(parseAttrSales(hasNestedEntity), changeId);
        return update.longValue();
    }

    public List<PatchCommand> parseAttrSales(List<PatchCommand> hasNestedEntity) {
        Set<String> collect = hasNestedEntity.stream().map(e -> e.getPath().split("/")[1]).collect(Collectors.toSet());
        String join = "id:" + String.join(".", collect);
        SumPagedRep<AppProductCardRep> appProductCardRepSumPagedRep = readByQuery(join, null, "sc:1");
        hasNestedEntity.forEach(e -> {
            String[] split = e.getPath().split("/");
            String id = split[1];
            String fieldName = split[split.length - 1];
            String attrSales = parseAttrSales(e);
            Optional<AppProductCardRep> first = appProductCardRepSumPagedRep.getData().stream().filter(ee -> ee.getId().toString().equals(id)).findFirst();
            if (first.isPresent()) {
                Long aLong = first.get().getAttrSalesMap().get(attrSales);
                e.setPath("/" + aLong + "/" + fieldName);
            }
        });

        return null;
    }

    /**
     * @param command [{"op":"add","path":"/837195323695104/skus?query=attributesSales:835604723556352-淡粉色,835604663263232-185~/100A~/XXL/storageActual","value":"1"}]
     * @return 835604723556352:淡粉色,835604663263232:185/100A/XXL
     */
    private String parseAttrSales(PatchCommand command) {
        String replace = command.getPath().replace("/" + ADMIN_REP_SKU_LITERAL + "?" + HTTP_PARAM_QUERY + "=" + ADMIN_REP_ATTR_SALES_LITERAL + ":", "");
        String replace1 = replace.replace("~/", "$");
        String[] split = replace1.split("/");
        if (split.length != 2)
            throw new NoUpdatableFieldException();
        String $ = split[0].replace("-", ":").replace("$", "/");
        return Arrays.stream($.split(",")).sorted((a, b) -> {
            long l = Long.parseLong(a.split(":")[0]);
            long l1 = Long.parseLong(b.split(":")[0]);
            return Long.compare(l, l1);
        }).collect(Collectors.joining(","));
    }

    @Transactional
    public void rollbackChangeForApp(String id) {
        log.info("start of rollback change {}", id);
        if (changeHistoryRepository.findByChangeIdAndEntityType(id + CHANGE_REVOKED, entityClass.getName()).isPresent()) {
            throw new HangingTransactionException();
        }
        Optional<ChangeRecord> byChangeId = changeHistoryRepository.findByChangeIdAndEntityType(id, entityClass.getName());
        if (byChangeId.isPresent()) {
            ChangeRecord changeRecord = byChangeId.get();
            List<PatchCommand> rollbackCmd = buildRollbackCommand(changeRecord.getPatchCommands());
            patchForAppBatch(rollbackCmd, id + CHANGE_REVOKED);
        }
    }

    private List<PatchCommand> buildRollbackCommand(List<PatchCommand> patchCommands) {
        List<PatchCommand> deepCopy = getDeepCopy(patchCommands);
        deepCopy.forEach(e -> {
            if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_SUM)) {
                e.setOp(PATCH_OP_TYPE_DIFF);
            } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF)) {
                e.setOp(PATCH_OP_TYPE_SUM);
            } else {
                throw new RollbackNotSupportedException();
            }
        });
        return deepCopy;
    }


}

