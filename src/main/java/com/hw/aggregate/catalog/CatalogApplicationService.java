package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.model.Catalog;
import com.hw.aggregate.catalog.representation.CatalogRepresentation;
import com.hw.aggregate.catalog.representation.CatalogTreeAdminRepresentation;
import com.hw.aggregate.catalog.representation.CatalogTreeCustomerRepresentation;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogApplicationService {

    @Autowired
    private CatalogRepo repo;

    @Autowired
    private IdGenerator idGenerator;

    public CatalogTreeCustomerRepresentation getAllForCustomer() {
        return new CatalogTreeCustomerRepresentation(repo.findAll());
    }

    public CatalogTreeAdminRepresentation getAllForAdmin() {
        return new CatalogTreeAdminRepresentation(repo.findAll());
    }

    public CatalogRepresentation create(CreateCatalogCommand command) {
        return new CatalogRepresentation(Catalog.create(idGenerator.getId(), command, repo));
    }

    public void update(Long id, UpdateCatalogCommand command) {
        Catalog.update(id, command, repo);
    }

    public void delete(Long id) {
        Catalog.delete(id, repo);
    }

}
