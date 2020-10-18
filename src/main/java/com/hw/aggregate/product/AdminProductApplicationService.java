package com.hw.aggregate.product;

import com.hw.aggregate.attribute.AppBizAttributeApplicationService;
import com.hw.aggregate.catalog.PublicBizCatalogApplicationService;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.model.AdminProductPatchMiddleLayer;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.representation.AdminProductCardRep;
import com.hw.aggregate.product.representation.AdminProductRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;

@Slf4j
@Service
public class AdminProductApplicationService extends DefaultRoleBasedRestfulService<Product, AdminProductCardRep, AdminProductRep, AdminProductPatchMiddleLayer> {


    @Autowired
    private PublicBizCatalogApplicationService catalogApplicationService;

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @Autowired
    private AppBizAttributeApplicationService attributeApplicationService;

    @Autowired
    private AppProductApplicationService appProductApplicationService;


    @PostConstruct
    private void setUp() {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = (AdminProductPatchMiddleLayer::new);
    }

    @Override
    @Transactional
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> hasNestedEntity = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        appBizSkuApplicationService.patchBatch(Product.convertToSkuCommands(hasNestedEntity, appProductApplicationService), changeId);
        return super.patchBatch(noNestedEntity, changeId);
    }

    @Override
    @Transactional
    public Integer deleteByQuery(String query, String changeId) {
        List<AdminProductCardRep> data = getAllByQuery(query);
        Set<String> collect = data.stream().map(e -> e.getId().toString()).collect(Collectors.toSet());
        String join = SKU_REFERENCE_ID_LITERAL + ":" + String.join(".", collect);
        appBizSkuApplicationService.deleteByQuery(join, changeId);
        return queryRegistry.deleteByQuery(role, query, Product.class);
    }


    @Override
    @Transactional
    public Integer deleteById(Long id, String changeId) {
        appBizSkuApplicationService.deleteByQuery(SKU_REFERENCE_ID_LITERAL + ":" + id, changeId);
        return queryRegistry.deleteById(role, id.toString(), Product.class);
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        product.replace((AdminUpdateProductCommand) command, appBizSkuApplicationService);
        return product;
    }

    @Override
    public AdminProductCardRep getEntitySumRepresentation(Product product) {
        return new AdminProductCardRep(product);
    }

    @Override
    public AdminProductRep getEntityRepresentation(Product product) {
        return new AdminProductRep(product, appBizSkuApplicationService);
    }


    @Override
    protected Product createEntity(long id, Object command) {
        return Product.create(id, (AdminCreateProductCommand) command, appBizSkuApplicationService);
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

