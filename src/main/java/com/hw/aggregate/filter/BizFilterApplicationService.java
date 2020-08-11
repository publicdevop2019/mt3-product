package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.exception.BizFilterNotFoundException;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.BizFilterManager;
import com.hw.aggregate.filter.representation.BizFilterAdminRep;
import com.hw.aggregate.filter.representation.BizFilterAdminSumRep;
import com.hw.aggregate.filter.representation.BizFilterCreatedRep;
import com.hw.aggregate.filter.representation.BizFilterPublicRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BizFilterApplicationService {
    @Autowired
    private BizFilterRepository repo;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private BizFilterManager bizFilterManager;

    @Transactional
    public BizFilterCreatedRep createForAdmin(CreateBizFilterCommand command) {
        BizFilter bizFilter = BizFilter.create(idGenerator.getId(), command, repo);
        return new BizFilterCreatedRep(bizFilter);
    }

    @Transactional
    public void replaceForAdmin(Long filterId, UpdateBizFilterCommand command) {
        SumPagedRep<BizFilter> bizFilterSumPagedRep = bizFilterManager.readById(RestfulEntityManager.RoleEnum.ADMIN, filterId.toString(), BizFilter.class);
        if (bizFilterSumPagedRep.getData().size() == 0)
            throw new BizFilterNotFoundException();
        bizFilterSumPagedRep.getData().get(0).replace(command, repo);
    }

    @Transactional
    public void deleteForAdminById(Long filterId) {
        bizFilterManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, filterId.toString(), BizFilter.class);
    }

    @Transactional(readOnly = true)
    public BizFilterAdminRep readForAdminById(Long filterId) {
        SumPagedRep<BizFilter> bizFilterSumPagedRep = bizFilterManager.readById(RestfulEntityManager.RoleEnum.ADMIN, filterId.toString(), BizFilter.class);
        if (bizFilterSumPagedRep.getData().size() == 0)
            throw new BizFilterNotFoundException();
        return new BizFilterAdminRep(bizFilterSumPagedRep.getData().get(0));
    }

    @Transactional(readOnly = true)
    public BizFilterAdminSumRep readForAdminByQuery(String search, String page, String countFlag) {
        SumPagedRep<BizFilter> bizFilterSumPagedRep = bizFilterManager.readByQuery(RestfulEntityManager.RoleEnum.ADMIN, search, page, countFlag, BizFilter.class);
        return new BizFilterAdminSumRep(bizFilterSumPagedRep);
    }

    @Transactional(readOnly = true)
    public BizFilterPublicRep readForPublicByQuery(String search, String page, String countFlag) {
        SumPagedRep<BizFilter> bizFilterSumPagedRep = bizFilterManager.readByQuery(RestfulEntityManager.RoleEnum.PUBLIC, search, page, countFlag, BizFilter.class);
        return new BizFilterPublicRep(bizFilterSumPagedRep);
    }

}
