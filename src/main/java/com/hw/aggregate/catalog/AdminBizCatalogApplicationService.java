package com.hw.aggregate.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.catalog.command.CreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateBizCatalogCommand;
import com.hw.aggregate.catalog.model.AdminBizCatalogPatchMiddleLayer;
import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.model.BizCatalogQueryRegistry;
import com.hw.aggregate.catalog.representation.AdminBizCatalogCardRep;
import com.hw.aggregate.catalog.representation.AdminBizCatalogRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class AdminBizCatalogApplicationService extends DefaultRoleBasedRestfulService<BizCatalog, AdminBizCatalogCardRep, AdminBizCatalogRep, AdminBizCatalogPatchMiddleLayer> {

    @Autowired
    private BizCatalogRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizCatalogQueryRegistry registry;
    @Autowired
    private ObjectMapper om2;
    @Autowired
    private AppChangeRecordApplicationService changeHistoryRepository;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = registry;
        entityClass = BizCatalog.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizCatalogPatchMiddleLayer::new;
        om = om2;
        appChangeRecordApplicationService = changeHistoryRepository;
    }

    @Override
    public BizCatalog replaceEntity(BizCatalog catalog, Object command) {
        catalog.replace((UpdateBizCatalogCommand) command);
        return catalog;
    }

    @Override
    public AdminBizCatalogCardRep getEntitySumRepresentation(BizCatalog catalog) {
        return new AdminBizCatalogCardRep(catalog);
    }

    @Override
    public AdminBizCatalogRep getEntityRepresentation(BizCatalog catalog) {
        return new AdminBizCatalogRep(catalog);
    }

    @Override
    protected BizCatalog createEntity(long id, Object command) {
        return BizCatalog.create(idGenerator.getId(), (CreateBizCatalogCommand) command);
    }

    @Override
    public void preDelete(BizCatalog bizCatalog) {

    }

    @Override
    public void postDelete(BizCatalog bizCatalog) {

    }

    @Override
    protected void prePatch(BizCatalog bizCatalog, Map<String, Object> params, AdminBizCatalogPatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(BizCatalog bizCatalog, Map<String, Object> params, AdminBizCatalogPatchMiddleLayer middleLayer) {

    }
}
