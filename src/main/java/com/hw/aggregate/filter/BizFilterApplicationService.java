package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminSelectQueryBuilder;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.CustomerQueryBuilder;
import com.hw.aggregate.filter.representation.BizFilterAdminRepresentation;
import com.hw.aggregate.filter.representation.BizFilterAdminSummaryRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCreatedRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCustomerRepresentation;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.DefaultSumPagedRep;
import com.hw.shared.IdGenerator;
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

@Service
public class BizFilterApplicationService extends DefaultApplicationService {
    @Autowired
    private BizFilterRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AdminSelectQueryBuilder adminQueryBuilder;

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
    public BizFilterAdminSummaryRepresentation adminQuery(String search, String page, String countFlag) {
        DefaultSumPagedRep<BizFilter> select1 = select(adminQueryBuilder, search, page, countFlag, BizFilter.class);
        return new BizFilterAdminSummaryRepresentation(select1);
    }

    @Transactional(readOnly = true)
    public BizFilterCustomerRepresentation customerQuery(String search, String page, String countFlag) {
        DefaultSumPagedRep<BizFilter> select1 = select(customerQueryBuilder, search, page, countFlag, BizFilter.class);
        return new BizFilterCustomerRepresentation(select1);
    }
}
