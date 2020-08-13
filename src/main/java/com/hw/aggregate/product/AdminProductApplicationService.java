package com.hw.aggregate.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.catalog.PublicCatalogApplicationService;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.exception.HangingTransactionException;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.AdminProductCardRep;
import com.hw.aggregate.product.representation.AdminProductRep;
import com.hw.shared.DeepCopyException;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.config.AppConstant.REVOKE;

@Slf4j
@Service
public class AdminProductApplicationService extends DefaultRoleBasedRestfulService<Product, AdminProductCardRep, AdminProductRep> {

    @Autowired
    private ProductRepo repo2;
    @Autowired
    private ChangeRecordRepository changeHistoryRepository;

    @Autowired
    private PublicCatalogApplicationService catalogApplicationService;

    @Autowired
    private AppBizAttributeApplicationService attributeApplicationService;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private ProductManager productDetailManager;
    @Autowired
    private ProductSkuManager productSkuManager;

    @Autowired
    private ObjectMapper om;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = productDetailManager;
        entityClass = Product.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
    }

    @Transactional
    public AdminProductRep patchById(Long id, JsonPatch patch) {
        SumPagedRep<Product> productDetailSumPagedRep = productDetailManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), Product.class);
        if (productDetailSumPagedRep.getData().size() == 0)
            throw new ProductNotFoundException();
        Product product = productDetailSumPagedRep.getData().get(0);
        return new AdminProductRep(ProductPatchMiddleLayer.doPatch(patch, product, om, repo2));
    }

    @Transactional
    public Long patch(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeId(changeId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer update1 = productSkuManager.update(RestfulEntityManager.RoleEnum.ADMIN, hasNestedEntity, ProductSku.class);
        Integer update = productDetailManager.update(RestfulEntityManager.RoleEnum.ADMIN, noNestedEntity, Product.class);
        return update.longValue();
    }

    @Override
    @Transactional
    public Integer deleteByQuery(String query) {
        //delete sku first
        productSkuManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, ProductSku.class);
        return productDetailManager.deleteByQuery(RestfulEntityManager.RoleEnum.ADMIN, query, Product.class);
    }

    @Override
    @Transactional
    public Integer deleteById(Long id) {
        productSkuManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), ProductSku.class);
        return productDetailManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), Product.class);
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

    @Override
    public Product replaceEntity(Product product, Object command) {
        product.replace((AdminUpdateProductCommand) command, this);
        return product;
    }

    @Override
    public AdminProductCardRep getEntitySumRepresentation(Product product) {
        return new AdminProductCardRep(product);
    }

    @Override
    public AdminProductRep getEntityRepresentation(Product product) {
        return new AdminProductRep(product);
    }

    @Override
    protected CreatedEntityRep getCreatedEntityRepresentation(Product created) {
        return new CreatedEntityRep(created);
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return Product.create(id, (AdminCreateProductCommand) command);
    }
}

