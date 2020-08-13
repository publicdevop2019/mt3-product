package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.model.BizCatalogQueryRegistry;
import com.hw.aggregate.catalog.representation.PublicBizCatalogCardRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulEntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PublicBizCatalogApplicationService extends DefaultRoleBasedRestfulService<BizCatalog, PublicBizCatalogCardRep, Void, VoidTypedClass> {

    @Autowired
    private BizCatalogRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizCatalogQueryRegistry catalogManager2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = catalogManager2;
        entityClass = BizCatalog.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
    }

    @Override
    public BizCatalog replaceEntity(BizCatalog catalog, Object command) {
        return null;
    }

    @Override
    public PublicBizCatalogCardRep getEntitySumRepresentation(BizCatalog catalog) {
        return new PublicBizCatalogCardRep(catalog);
    }

    @Override
    public Void getEntityRepresentation(BizCatalog catalog) {
        return null;
    }

    @Override
    protected BizCatalog createEntity(long id, Object command) {
        return null;
    }
}
