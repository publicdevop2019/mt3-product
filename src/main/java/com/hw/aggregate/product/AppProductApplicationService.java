package com.hw.aggregate.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductQueryRegistry;
import com.hw.aggregate.product.representation.AppProductCardRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
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

@Slf4j
@Service
public class AppProductApplicationService extends DefaultRoleBasedRestfulService<Product, AppProductCardRep, Void, VoidTypedClass> {

    @Autowired
    private ProductRepo repo2;
    @Autowired
    private AppChangeRecordApplicationService changeHistoryRepository;

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
        appChangeRecordApplicationService = changeHistoryRepository;
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


    @Override
    @Transactional
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> hasNestedEntity = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> noNestedEntity = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        if (hasNestedEntity.size() > 0)
            appBizSkuApplicationService.patchBatch(Product.convertToSkuCommands(hasNestedEntity, this), changeId);
        return super.patchBatch(noNestedEntity, changeId);
    }

}

