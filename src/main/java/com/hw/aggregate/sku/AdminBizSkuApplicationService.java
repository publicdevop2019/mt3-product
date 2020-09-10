package com.hw.aggregate.sku;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.sku.command.AdminCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AdminUpdateBizSkuCommand;
import com.hw.aggregate.sku.model.AdminBizSkuPatchMiddleLayer;
import com.hw.aggregate.sku.model.BizSku;
import com.hw.aggregate.sku.model.BizSkuQueryRegistry;
import com.hw.aggregate.sku.representation.AdminBizSkuCardRep;
import com.hw.aggregate.sku.representation.AdminBizSkuRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class AdminBizSkuApplicationService extends DefaultRoleBasedRestfulService<BizSku, AdminBizSkuCardRep, AdminBizSkuRep, AdminBizSkuPatchMiddleLayer> {
    @Autowired
    private BizSkuRepo repo2;
    @Autowired
    private AppChangeRecordApplicationService changeHistoryRepository;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizSkuQueryRegistry skuQueryRegistry;

    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = skuQueryRegistry;
        entityClass = BizSku.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = (AdminBizSkuPatchMiddleLayer::new);
        om = om2;
        appChangeRecordApplicationService = changeHistoryRepository;
    }

    @Override
    public BizSku replaceEntity(BizSku bizSku, Object command) {
        return bizSku.replace((AdminUpdateBizSkuCommand)command);
    }

    @Override
    public AdminBizSkuCardRep getEntitySumRepresentation(BizSku bizSku) {
        return new AdminBizSkuCardRep(bizSku);
    }

    @Override
    public AdminBizSkuRep getEntityRepresentation(BizSku bizSku) {
        return new AdminBizSkuRep(bizSku);
    }

    @Override
    protected BizSku createEntity(long id, Object command) {
        return BizSku.create(id,(AdminCreateBizSkuCommand)command);
    }

    @Override
    public void preDelete(BizSku bizSku) {

    }

    @Override
    public void postDelete(BizSku bizSku) {

    }

    @Override
    protected void prePatch(BizSku bizSku, Map<String, Object> params, AdminBizSkuPatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(BizSku bizSku, Map<String, Object> params, AdminBizSkuPatchMiddleLayer middleLayer) {

    }

}
