package com.hw.aggregate.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.catalog.command.CreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateBizCatalogCommand;
import com.hw.aggregate.catalog.model.AdminBizCatalogPatchMiddleLayer;
import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.model.BizCatalogManager;
import com.hw.aggregate.catalog.representation.AdminBizCatalogCardRep;
import com.hw.aggregate.catalog.representation.AdminBizCatalogRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulEntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class AdminBizCatalogApplicationService extends DefaultRoleBasedRestfulService<BizCatalog, AdminBizCatalogCardRep, AdminBizCatalogRep, AdminBizCatalogPatchMiddleLayer> {

    @Autowired
    private BizCatalogRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizCatalogManager catalogManager2;
    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = catalogManager2;
        entityClass = BizCatalog.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizCatalogPatchMiddleLayer::new;
        om = om2;
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
    protected CreatedRep getCreatedEntityRepresentation(BizCatalog created) {
        return new CreatedEntityRep(created);
    }

    @Override
    protected BizCatalog createEntity(long id, Object command) {
        return BizCatalog.create(idGenerator.getId(), (CreateBizCatalogCommand) command);
    }
}
