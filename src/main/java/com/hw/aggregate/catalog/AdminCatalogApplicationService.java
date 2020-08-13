package com.hw.aggregate.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.AdminCatalogPatchMiddleLayer;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogManager;
import com.hw.aggregate.catalog.representation.AdminCatalogCardRep;
import com.hw.aggregate.catalog.representation.AdminCatalogRep;
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
public class AdminCatalogApplicationService extends DefaultRoleBasedRestfulService<Catalog, AdminCatalogCardRep, AdminCatalogRep, AdminCatalogPatchMiddleLayer> {

    @Autowired
    private CatalogRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private CatalogManager catalogManager2;
    @Autowired
    private ObjectMapper om2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = catalogManager2;
        entityClass = Catalog.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
        entityPatchSupplier = AdminCatalogPatchMiddleLayer::new;
        om = om2;
    }

    @Override
    public Catalog replaceEntity(Catalog catalog, Object command) {
        catalog.replace((UpdateCatalogCommand) command);
        return catalog;
    }

    @Override
    public AdminCatalogCardRep getEntitySumRepresentation(Catalog catalog) {
        return new AdminCatalogCardRep(catalog);
    }

    @Override
    public AdminCatalogRep getEntityRepresentation(Catalog catalog) {
        return new AdminCatalogRep(catalog);
    }

    @Override
    protected CreatedRep getCreatedEntityRepresentation(Catalog created) {
        return new CreatedEntityRep(created);
    }

    @Override
    protected Catalog createEntity(long id, Object command) {
        return Catalog.create(idGenerator.getId(), (CreateCatalogCommand) command);
    }
}
