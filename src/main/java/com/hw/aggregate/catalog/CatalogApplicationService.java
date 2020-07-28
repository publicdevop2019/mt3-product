package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.AdminSortConfig;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.model.CatalogType;
import com.hw.aggregate.catalog.model.CustomerSortConfig;
import com.hw.aggregate.catalog.representation.CatalogCreatedRepresentation;
import com.hw.aggregate.catalog.representation.CatalogTreeAdminRepresentation;
import com.hw.aggregate.catalog.representation.CatalogTreeCustomerRepresentation;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional(readOnly = true)
    public CatalogTreeCustomerRepresentation getAllForCustomer(Integer pageNumber, Integer pageSize, CustomerSortConfig sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = CustomerSortConfig.getPageRequestCustomer(pageNumber, pageSize, sortBy, sortOrder);
        return new CatalogTreeCustomerRepresentation(repo.findByType(entityManager, CatalogType.FRONTEND, pageRequestCustomer));
    }

    @Transactional(readOnly = true)
    public CatalogTreeAdminRepresentation getAllForAdminBackend(Integer pageNumber, Integer pageSize, AdminSortConfig sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = AdminSortConfig.getPageRequestAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return new CatalogTreeAdminRepresentation(repo.findByType(entityManager, CatalogType.BACKEND, pageRequestCustomer));
    }

    @Transactional(readOnly = true)
    public CatalogTreeAdminRepresentation getAllForAdminFrontend(Integer pageNumber, Integer pageSize, AdminSortConfig sortBy, SortOrder sortOrder) {
        PageRequest pageRequestCustomer = AdminSortConfig.getPageRequestAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return new CatalogTreeAdminRepresentation(repo.findByType(entityManager, CatalogType.FRONTEND, pageRequestCustomer));
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

}
