package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogManager;
import com.hw.aggregate.catalog.representation.PublicCatalogCardRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulEntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PublicCatalogApplicationService extends DefaultRoleBasedRestfulService<Catalog, PublicCatalogCardRep, Void,Void> {

    @Autowired
    private CatalogRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private CatalogManager catalogManager2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = catalogManager2;
        entityClass = Catalog.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
    }

    @Override
    public Catalog replaceEntity(Catalog catalog, Object command) {
        return null;
    }

    @Override
    public PublicCatalogCardRep getEntitySumRepresentation(Catalog catalog) {
        return new PublicCatalogCardRep(catalog);
    }

    @Override
    public Void getEntityRepresentation(Catalog catalog) {
        return null;
    }

    @Override
    protected <S extends CreatedRep> S getCreatedEntityRepresentation(Catalog created) {
        return null;
    }

    @Override
    protected Catalog createEntity(long id, Object command) {
        return null;
    }
}
