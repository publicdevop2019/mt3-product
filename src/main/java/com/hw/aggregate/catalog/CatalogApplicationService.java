package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.exception.CatalogNotFoundException;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogManager;
import com.hw.aggregate.catalog.representation.CatalogAdminRep;
import com.hw.aggregate.catalog.representation.CatalogAdminSummaryRep;
import com.hw.aggregate.catalog.representation.CatalogCreatedRep;
import com.hw.aggregate.catalog.representation.CatalogPublicSummaryRep;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.IdGenerator;
import com.hw.shared.RestfulEntityManager;
import com.hw.shared.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CatalogApplicationService extends DefaultApplicationService {

    @Autowired
    private CatalogRepository repo;

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private CatalogManager catalogManager;

    @Transactional(readOnly = true)
    public CatalogPublicSummaryRep readForPublicByQuery(String search, String page, String countFlag) {
        SumPagedRep<Catalog> catalogSumPagedRep = catalogManager.readByQuery(RestfulEntityManager.RoleEnum.PUBLIC, search, page, countFlag, Catalog.class);
        return new CatalogPublicSummaryRep(catalogSumPagedRep);
    }

    @Transactional(readOnly = true)
    public CatalogAdminSummaryRep readForAdminByQuery(String search, String page, String countFlag) {
        SumPagedRep<Catalog> catalogSumPagedRep = catalogManager.readByQuery(RestfulEntityManager.RoleEnum.ADMIN, search, page, countFlag, Catalog.class);
        return new CatalogAdminSummaryRep(catalogSumPagedRep);
    }

    @Transactional
    public CatalogCreatedRep createForAdmin(CreateCatalogCommand command) {
        return new CatalogCreatedRep(Catalog.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void replaceForAdminById(Long id, UpdateCatalogCommand command) {
        SumPagedRep<Catalog> catalogSumPagedRep = catalogManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), Catalog.class);
        if (catalogSumPagedRep.getData().size() == 0)
            throw new CatalogNotFoundException();
        catalogSumPagedRep.getData().get(0).update(command, repo);
    }

    @Transactional
    public void deleteForAdminById(Long id) {
        catalogManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), Catalog.class);
    }

    @Transactional(readOnly = true)
    public CatalogAdminRep readForAdminById(Long id) {
        SumPagedRep<Catalog> catalogSumPagedRep = catalogManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), Catalog.class);
        if (catalogSumPagedRep.getData().size() == 0)
            throw new CatalogNotFoundException();
        return new CatalogAdminRep(catalogSumPagedRep.getData().get(0));
    }
}
