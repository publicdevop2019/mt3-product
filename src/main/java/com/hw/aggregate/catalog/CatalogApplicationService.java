package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.AdminQueryBuilder;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CustomerQueryBuilder;
import com.hw.aggregate.catalog.representation.CatalogAdminRepresentation;
import com.hw.aggregate.catalog.representation.CatalogAdminSummaryRepresentation;
import com.hw.aggregate.catalog.representation.CatalogCreatedRepresentation;
import com.hw.aggregate.catalog.representation.CatalogCustomerSummaryRepresentation;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.DefaultSumPagedRep;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@Service
public class CatalogApplicationService extends DefaultApplicationService {

    @Autowired
    private CatalogRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminQueryBuilder adminQueryBuilder;

    @Transactional(readOnly = true)
    public CatalogCustomerSummaryRepresentation customerQuery(String search, String page, String countFlag) {
        DefaultSumPagedRep<Catalog> select = select(customerQueryBuilder, search, page, countFlag, Catalog.class);
        return new CatalogCustomerSummaryRepresentation(select);
    }

    @Transactional(readOnly = true)
    public CatalogAdminSummaryRepresentation adminQuery(String search, String page, String countFlag) {
        DefaultSumPagedRep<Catalog> select = select(adminQueryBuilder, search, page, countFlag, Catalog.class);
        return new CatalogAdminSummaryRepresentation(select);
    }

    @Transactional
    public CatalogCreatedRepresentation create(CreateCatalogCommand command) {
        return new CatalogCreatedRepresentation(Catalog.create(idGenerator.getId(), command, repo));
    }

    @Transactional
    public void update(Long id, UpdateCatalogCommand command) {
        Catalog read = Catalog.read(id, repo);
        read.update(command, repo);
    }

    @Transactional
    public void delete(Long id) {
        Catalog.delete(id, repo);
    }

    @Transactional(readOnly = true)
    public CatalogAdminRepresentation read(Long id) {
        return new CatalogAdminRepresentation(Catalog.read(id, repo));
    }
}
