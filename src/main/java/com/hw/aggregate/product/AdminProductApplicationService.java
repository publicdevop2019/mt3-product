package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.catalog.PublicBizCatalogApplicationService;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.AdminProductCardRep;
import com.hw.aggregate.product.representation.AdminProductRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.idempotent.exception.HangingTransactionException;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.config.AppConstant.REVOKE;

@Slf4j
@Service
public class AdminProductApplicationService extends DefaultRoleBasedRestfulService<Product, AdminProductCardRep, AdminProductRep, AdminProductPatchMiddleLayer> {

    @Autowired
    private ProductRepo repo2;
    @Autowired
    private ChangeRepository changeHistoryRepository;

    @Autowired
    private PublicBizCatalogApplicationService catalogApplicationService;

    @Autowired
    private AppBizAttributeApplicationService attributeApplicationService;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private ProductQueryRegistry productDetailManager;
    @Autowired
    private ProductSkuQueryRegistry productSkuManager;

    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = productDetailManager;
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = (AdminProductPatchMiddleLayer::new);
        om = om2;
        changeRepository = changeHistoryRepository;
    }

    @Override
    @Transactional
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        if (changeHistoryRepository.findByChangeId(changeId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        List<PatchCommand> hasNestedEntity = deepCopy.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = deepCopy.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        productSkuManager.update(role, hasNestedEntity, ProductSku.class);
        return productDetailManager.update(role, noNestedEntity, Product.class);
    }

    @Override
    @Transactional
    public Integer deleteByQuery(String query) {
        //delete sku first
        productSkuManager.deleteByQuery(role, query, ProductSku.class);
        return productDetailManager.deleteByQuery(role, query, Product.class);
    }

    @Override
    @Transactional
    public Integer deleteById(Long id) {
        productSkuManager.deleteById(role, id.toString(), ProductSku.class);
        return productDetailManager.deleteById(role, id.toString(), Product.class);
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
    protected Product createEntity(long id, Object command) {
        return Product.create(id, (AdminCreateProductCommand) command);
    }

    @Override
    public void preDelete(Product product) {

    }

    @Override
    public void postDelete(Product product) {

    }

    @Override
    protected void prePatch(Product product, Map<String, Object> params, AdminProductPatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(Product product, Map<String, Object> params, AdminProductPatchMiddleLayer middleLayer) {

    }
}

