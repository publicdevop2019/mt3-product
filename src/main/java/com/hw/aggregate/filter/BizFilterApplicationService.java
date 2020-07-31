package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminQueryBuilder;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.CustomerQueryBuilder;
import com.hw.aggregate.filter.representation.BizFilterAdminRepresentation;
import com.hw.aggregate.filter.representation.BizFilterAdminSummaryRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCreatedRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCustomerRepresentation;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
public class BizFilterApplicationService {
    @Autowired
    private BizFilterRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AdminQueryBuilder adminQueryBuilder;

    @Autowired
    private CustomerQueryBuilder customerQueryBuilder;

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
    public BizFilterAdminSummaryRepresentation adminQuery(String query, String page, String countFlag) {
        PageRequest pageRequest = adminQueryBuilder.getPageRequest(page);
        Predicate queryClause = adminQueryBuilder.getQueryClause(query);
        List<BizFilter> query1 = repo.query(entityManager, queryClause, pageRequest);
        Long aLong = null;
        if ("0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, queryClause);
        }
        return new BizFilterAdminSummaryRepresentation(query1, aLong);
    }

    @Transactional(readOnly = true)
    public BizFilterCustomerRepresentation customerQuery(String query, String page, String countFlag) {
        PageRequest pageRequest = customerQueryBuilder.getPageRequest(page);
        Predicate queryClause = customerQueryBuilder.getQueryClause(query);
        List<BizFilter> bizFilters = repo.query(entityManager, queryClause, pageRequest);
        if (bizFilters.size() == 0)
            return new BizFilterCustomerRepresentation(null);
        return new BizFilterCustomerRepresentation(bizFilters.get(0));
    }
}
