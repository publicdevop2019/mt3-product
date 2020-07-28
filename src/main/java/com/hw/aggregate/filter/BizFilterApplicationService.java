package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminQueryConfig;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.representation.BizFilterAdminRepresentation;
import com.hw.aggregate.filter.representation.BizFilterAdminSummaryRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCreatedRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCustomerRepresentation;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class BizFilterApplicationService {
    @Autowired
    private BizFilterRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public BizFilterCreatedRepresentation create(CreateBizFilterCommand command) {
        BizFilter bizFilter = BizFilter.create(idGenerator.getId(), command, repo);
        return new BizFilterCreatedRepresentation(bizFilter);
    }

    @Transactional
    public void update(Long filterId, UpdateBizFilterCommand command) {
        BizFilter read = BizFilter.read(filterId, repo);
        read.update(command, repo);
    }

    @Transactional
    public void delete(Long filterId) {
        BizFilter.delete(filterId, repo);
    }

    @Transactional(readOnly = true)
    public BizFilterAdminRepresentation getById(Long filterId) {
        return new BizFilterAdminRepresentation(BizFilter.read(filterId, repo));
    }

    @Transactional(readOnly = true)
    public BizFilterAdminSummaryRepresentation getAll(Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        Page<BizFilter> all = repo.findAll(AdminQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder));
        return new BizFilterAdminSummaryRepresentation(all.getContent(), all.getTotalPages(), all.getTotalElements());
    }

    @Transactional(readOnly = true)
    public BizFilterCustomerRepresentation getByCatalog(String catalog) {
        List<BizFilter> bizFilters = repo.searchByAttributesDynamic(entityManager, catalog);
        return new BizFilterCustomerRepresentation(bizFilters.get(0));
    }
}
