package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.BizFilterQueryRegistry;
import com.hw.aggregate.filter.representation.AdminBizFilterRep;
import com.hw.aggregate.filter.representation.AdminBizFilterCardRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AdminBizFilterApplicationService extends DefaultRoleBasedRestfulService<BizFilter, AdminBizFilterCardRep, AdminBizFilterRep, VoidTypedClass> {
    @Autowired
    private BizFilterRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizFilterQueryRegistry bizFilterManager2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = bizFilterManager2;
        entityClass = BizFilter.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
    }

    @Override
    public BizFilter replaceEntity(BizFilter bizFilter, Object command) {
        bizFilter.replace((UpdateBizFilterCommand) command);
        return bizFilter;
    }

    @Override
    public AdminBizFilterCardRep getEntitySumRepresentation(BizFilter bizFilter) {
        return new AdminBizFilterCardRep(bizFilter);
    }

    @Override
    public AdminBizFilterRep getEntityRepresentation(BizFilter bizFilter) {
        return null;
    }

    @Override
    protected CreatedEntityRep getCreatedEntityRepresentation(BizFilter created) {
        return new CreatedEntityRep(created);
    }

    @Override
    protected BizFilter createEntity(long id, Object command) {
        return BizFilter.create(id, (CreateBizFilterCommand) command);
    }
}
