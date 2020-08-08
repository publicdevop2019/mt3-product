package com.hw.aggregate.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.CreateProductAdminCommand;
import com.hw.aggregate.product.command.ProductValidationCommand;
import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.DeepCopyException;
import com.hw.aggregate.product.exception.HangingTransactionException;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.exception.RollbackNotSupportedException;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import com.hw.shared.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.ProductDetailAdminRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.config.AppConstant.REVOKE;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_DIFF;
import static com.hw.shared.AppConstant.PATCH_OP_TYPE_SUM;

@Slf4j
@Service
public class ProductApplicationService {

    @Autowired
    private ProductDetailRepo repo;
    @Autowired
    private ChangeRecordRepository changeHistoryRepository;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @Autowired
    private BizAttributeApplicationService attributeApplicationService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ProductDetailManager productDetailManager;
    @Autowired
    private ProductSkuManager productSkuManager;

    @Autowired
    private ObjectMapper om;

    @Transactional(readOnly = true)
    public ProductAdminSumPagedRep readForAdminByQuery(String query, String page, String countFlag) {
        SumPagedRep<ProductDetail> pagedRep = productDetailManager.readByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, page, countFlag, ProductDetail.class);
        return new ProductAdminSumPagedRep(pagedRep);
    }

    @Transactional(readOnly = true)
    public ProductPublicSumPagedRep readForPublicByQuery(String query, String page, String countFlag) {
        SumPagedRep<ProductDetail> pagedRep = productDetailManager.readByQuery(RestfulEntityManager.RoleEnum.PUBLIC, query, page, countFlag, ProductDetail.class);
        return new ProductPublicSumPagedRep(pagedRep);
    }

    @Transactional(readOnly = true)
    public ProductDetailCustomRep readForPublicById(Long id) {
        SumPagedRep<ProductDetail> productDetailSumPagedRep = productDetailManager.readById(RestfulEntityManager.RoleEnum.PUBLIC, id.toString(), ProductDetail.class);
        if (productDetailSumPagedRep.getData().size() == 0)
            throw new ProductNotFoundException();
        return new ProductDetailCustomRep(productDetailSumPagedRep.getData().get(0), attributeApplicationService);
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRep readForAdminById(Long id) {
        ProductDetail productDetail = getForAdminProductDetail(id);
        return new ProductDetailAdminRep(productDetail);
    }

    @Transactional
    public ProductCreatedRep createForAdmin(CreateProductAdminCommand command) {
        return new ProductCreatedRep(ProductDetail.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void replaceForAdminById(Long id, UpdateProductAdminCommand command) {
        ProductDetail productDetail = getForAdminProductDetail(id);
        productDetail.replace(command, this, repo);
    }

    @Transactional
    public Integer deleteForAdminById(Long id) {
        productSkuManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductSku.class);
        return productDetailManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductDetail.class);
    }

    @Transactional
    public ProductDetailAdminRep patchForAdminById(Long id, JsonPatch patch) {
        ProductDetail productDetail = getForAdminProductDetail(id);
        return new ProductDetailAdminRep(ProductDetailPatchMiddleLayer.doPatch(patch, productDetail, om, repo));
    }

    @Transactional
    public ProductAdminSumPagedRep patchForAdmin(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeId(changeId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer update1 = productSkuManager.update(RestfulEntityManager.RoleEnum.ADMIN, hasNestedEntity, ProductSku.class);
        Integer update = productDetailManager.update(RestfulEntityManager.RoleEnum.ADMIN, noNestedEntity, ProductDetail.class);
        return new ProductAdminSumPagedRep(update.longValue());
    }

    @Transactional
    public ProductAppSumPagedRep patchForApp(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeId(changeId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer update = productDetailManager.update(RestfulEntityManager.RoleEnum.APP, noNestedEntity, ProductDetail.class);
        Integer update1 = productSkuManager.update(RestfulEntityManager.RoleEnum.APP, hasNestedEntity, ProductSku.class);
        return new ProductAppSumPagedRep(update.longValue());
    }


    @Transactional
    public Integer deleteForAdminByQuery(String query) {
        //delete sku first
        productSkuManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, ProductSku.class);
        return productDetailManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, ProductDetail.class);
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param commands
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRep validate(List<ProductValidationCommand> commands) {
        return new ProductValidationResultRep(ProductDetail.validate(commands, repo));
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


    private ProductDetail getForAdminProductDetail(Long id) {
        SumPagedRep<ProductDetail> productDetailSumPagedRep = productDetailManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductDetail.class);
        if (productDetailSumPagedRep.getData().size() == 0)
            throw new ProductNotFoundException();
        return productDetailSumPagedRep.getData().get(0);
    }

    private void saveChangeRecord(List<PatchCommand> details, String changeId) {
        ChangeRecord changeRecord = new ChangeRecord();
        changeRecord.setPatchCommands((ArrayList<PatchCommand>) details);
        changeRecord.setChangeId(changeId);
        changeRecord.setId(idGenerator.getId());
        changeHistoryRepository.save(changeRecord);
    }
}

