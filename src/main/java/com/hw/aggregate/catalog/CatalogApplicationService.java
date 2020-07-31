package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.AdminQueryConfig;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import com.hw.aggregate.catalog.model.CustomerQueryConfig;
import com.hw.aggregate.catalog.representation.CatalogAdminRepresentation;
import com.hw.aggregate.catalog.representation.CatalogAdminSummaryRepresentation;
import com.hw.aggregate.catalog.representation.CatalogCreatedRepresentation;
import com.hw.aggregate.catalog.representation.CatalogCustomerSummaryRepresentation;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@Service
public class CatalogApplicationService {

    @Autowired
    private CatalogRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerQueryConfig customerQueryConfig;

    @Autowired
    private AdminQueryConfig adminQueryConfig;

    @Transactional(readOnly = true)
    public CatalogCustomerSummaryRepresentation getAllForCustomer(Integer pageNumber, Integer pageSize, CustomerQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = customerQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Catalog> byType = repo.findByType(CatalogType.FRONTEND, pageRequestCustomer);
        return new CatalogCustomerSummaryRepresentation(byType.getContent(), byType.getTotalPages(), byType.getTotalElements());
    }

    @Transactional(readOnly = true)
    public CatalogAdminSummaryRepresentation getAllForAdminBackend(Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = adminQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Catalog> byType = repo.findByType(CatalogType.BACKEND, pageRequestCustomer);
        return new CatalogAdminSummaryRepresentation(byType.getContent(), byType.getTotalElements());
    }

    @Transactional(readOnly = true)
    public CatalogAdminSummaryRepresentation getAllForAdminFrontend(Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = adminQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Catalog> byType = repo.findByType(CatalogType.FRONTEND, pageRequestCustomer);
        return new CatalogAdminSummaryRepresentation(byType.getContent(), byType.getTotalElements());
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
