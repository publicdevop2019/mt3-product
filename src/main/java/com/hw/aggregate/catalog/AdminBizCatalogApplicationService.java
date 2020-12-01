package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.AdminCreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.AdminUpdateBizCatalogCommand;
import com.hw.aggregate.catalog.model.AdminBizCatalogPatchMiddleLayer;
import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.representation.AdminBizCatalogCardRep;
import com.hw.aggregate.catalog.representation.AdminBizCatalogRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class AdminBizCatalogApplicationService extends RoleBasedRestfulService<BizCatalog, AdminBizCatalogCardRep, AdminBizCatalogRep, AdminBizCatalogPatchMiddleLayer> {
    {
        entityClass = BizCatalog.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizCatalogPatchMiddleLayer::new;
    }

    @Override
    public BizCatalog replaceEntity(BizCatalog catalog, Object command) {
        catalog.replace((AdminUpdateBizCatalogCommand) command);
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
        return BizCatalog.create(idGenerator.getId(), (AdminCreateBizCatalogCommand) command);
    }

}
