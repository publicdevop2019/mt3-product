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
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
    private CustomerQueryBuilder customerQueryBuilder;

    @Autowired
    private AdminQueryBuilder adminQueryBuilder;

    @Transactional(readOnly = true)
    public CatalogCustomerSummaryRepresentation customerQuery(String query, String page, String countFlag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Catalog> query0 = cb.createQuery(Catalog.class);
        Root<Catalog> root = query0.from(Catalog.class);

        PageRequest pageRequest = customerQueryBuilder.getPageRequest(page);
        Predicate queryClause = customerQueryBuilder.getQueryClause(cb, root, query);

        List<Catalog> query1 = repo.query(entityManager, cb, query0, root, queryClause, pageRequest);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, cb, queryClause);
        }
        return new CatalogCustomerSummaryRepresentation(query1, aLong);
    }

    @Transactional(readOnly = true)
    public CatalogAdminSummaryRepresentation adminQuery(String query, String page, String countFlag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Catalog> query0 = cb.createQuery(Catalog.class);
        Root<Catalog> root = query0.from(Catalog.class);

        PageRequest pageRequest = adminQueryBuilder.getPageRequest(page);
        Predicate queryClause = adminQueryBuilder.getQueryClause(cb, root, query);

        List<Catalog> query1 = repo.query(entityManager, cb, query0, root, queryClause, pageRequest);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, cb, queryClause);
        }
        return new CatalogAdminSummaryRepresentation(query1, aLong);
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
