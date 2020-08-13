package com.hw.aggregate.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.product.exception.HangingTransactionException;
import com.hw.aggregate.product.exception.RollbackNotSupportedException;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.AppProductCardRep;
import com.hw.shared.DeepCopyException;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulEntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.config.AppConstant.REVOKE;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_DIFF;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_SUM;

@Slf4j
@Service
public class AppProductApplicationService extends DefaultRoleBasedRestfulService<Product, AppProductCardRep, Void, VoidTypedClass> {

    @Autowired
    private ProductRepo repo2;
    @Autowired
    private ChangeRecordRepository changeHistoryRepository;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private ProductManager productDetailManager;
    @Autowired
    private ProductSkuManager productSkuManager;

    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = productDetailManager;
        entityClass = Product.class;
        role = RestfulEntityManager.RoleEnum.APP;
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        return null;
    }

    @Override
    public AppProductCardRep getEntitySumRepresentation(Product product) {
        return new AppProductCardRep(product);
    }

    @Override
    public Void getEntityRepresentation(Product product) {
        return null;
    }

    @Override
    protected <S extends CreatedRep> S getCreatedEntityRepresentation(Product created) {
        return null;
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return null;
    }


    @Transactional
    public Long patchForApp(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeId(changeId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer update = productDetailManager.update(RestfulEntityManager.RoleEnum.APP, noNestedEntity, Product.class);
        Integer update1 = productSkuManager.update(RestfulEntityManager.RoleEnum.APP, hasNestedEntity, ProductSku.class);
        return update.longValue();
    }


    @Transactional
    public void rollbackChangeForApp(String id) {
        log.info("start of rollback change {}", id);
        if (changeHistoryRepository.findByChangeId(id + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        Optional<ChangeRecord> byChangeId = changeHistoryRepository.findByChangeId(id);
        if (byChangeId.isPresent()) {
            ChangeRecord changeRecord = byChangeId.get();
            List<PatchCommand> rollbackCmd = buildRollbackCommand(changeRecord.getPatchCommands());
            patchForApp(rollbackCmd, id + REVOKE);
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

    private List<PatchCommand> getDeepCopy(List<PatchCommand> patchCommands) {
        List<PatchCommand> deepCopy;
        try {
            deepCopy = om.readValue(om.writeValueAsString(patchCommands), new TypeReference<List<PatchCommand>>() {
            });
        } catch (IOException e) {
            log.error("error during deep copy", e);
            throw new DeepCopyException();
        }
        return deepCopy;
    }


    private void saveChangeRecord(List<PatchCommand> details, String changeId) {
        ChangeRecord changeRecord = new ChangeRecord();
        changeRecord.setPatchCommands((ArrayList<PatchCommand>) details);
        changeRecord.setChangeId(changeId);
        changeRecord.setId(idGenerator.getId());
        changeHistoryRepository.save(changeRecord);
    }

}

