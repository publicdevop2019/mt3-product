package com.hw.aggregate.product;

import com.mt.mall.application.catalog.PublicBizCatalogApplicationService;
import com.hw.aggregate.product.command.AdminCreateProductCommand;
import com.hw.aggregate.product.command.AdminUpdateProductCommand;
import com.hw.aggregate.product.model.AdminProductPatchMiddleLayer;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.representation.AdminProductCardRep;
import com.hw.aggregate.product.representation.AdminProductRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.mt.mall.application.tag.AppBizTagApplicationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;
import static com.hw.aggregate.sku.model.BizSku.SKU_REFERENCE_ID_LITERAL;

@Slf4j
@Service
public class AdminProductApplicationService extends RoleBasedRestfulService<Product, AdminProductCardRep, AdminProductRep, AdminProductPatchMiddleLayer> {
    {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = (AdminProductPatchMiddleLayer::new);
    }

    @Autowired
    private PublicBizCatalogApplicationService catalogApplicationService;

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @Autowired
    private AppBizTagApplicationService attributeApplicationService;

    @Autowired
    private AppProductApplicationService appProductApplicationService;
    @Autowired
    private TagRepo tagRepo;

    @Override
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> hasNestedEntity = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer execute = transactionTemplate.execute(transactionStatus -> {
            appBizSkuApplicationService.patchBatch(Product.convertToSkuCommands(hasNestedEntity, appProductApplicationService), changeId);
            return super.patchBatch(noNestedEntity, changeId);

        });
        cleanUpAllCache();
        appBizSkuApplicationService.cleanUpAllCache();
        return execute;
    }

    @Override
    public Integer deleteByQuery(String query, String changeId) {
        List<AdminProductCardRep> data = getAllByQuery(query);
        Set<Long> collect = data.stream().map(AdminProductCardRep::getId).collect(Collectors.toSet());
        String join = SKU_REFERENCE_ID_LITERAL + ":" + collect.stream().map(Object::toString).collect(Collectors.joining(","));
        Integer execute = transactionTemplate.execute(transactionStatus -> {
            appBizSkuApplicationService.deleteByQuery(join, changeId);
            return super.deleteByQuery(query, changeId);
        });
        cleanUpCache(collect);
        appBizSkuApplicationService.cleanUpAllCache();//need ids to clear cache more accurately
        return execute;
    }


    @Override
    public Integer deleteById(Long id, String changeId) {
        Integer execute = transactionTemplate.execute(transactionStatus -> {
            appBizSkuApplicationService.deleteByQuery(SKU_REFERENCE_ID_LITERAL + ":" + id, changeId);
            return super.deleteById(id, changeId);
        });
        cleanUpCache(Collections.singleton(id));
        appBizSkuApplicationService.cleanUpAllCache();//need ids to clear cache more accurately
        return execute;
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        product.replace((AdminUpdateProductCommand) command, appBizSkuApplicationService, tagRepo, idGenerator);
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
        return Product.create(id, (AdminCreateProductCommand) command, appBizSkuApplicationService, idGenerator, tagRepo);
    }

}

