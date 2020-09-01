package com.hw.aggregate.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminBizFilterPatchMiddleLayer;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.BizFilterQueryRegistry;
import com.hw.aggregate.filter.representation.AdminBizFilterCardRep;
import com.hw.aggregate.filter.representation.AdminBizFilterRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AdminBizFilterApplicationService extends DefaultRoleBasedRestfulService<BizFilter, AdminBizFilterCardRep, AdminBizFilterRep, AdminBizFilterPatchMiddleLayer> {
    @Autowired
    private BizFilterRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizFilterQueryRegistry bizFilterManager2;
    @Autowired
    private ChangeRepository changeHistoryRepository;
    @Autowired
    private ObjectMapper om2;
    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = bizFilterManager2;
        entityClass = BizFilter.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        changeRepository = changeHistoryRepository;
        entityPatchSupplier= AdminBizFilterPatchMiddleLayer::new;
        om=om2;
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
        return new AdminBizFilterRep(bizFilter);
    }

    @Override
    protected BizFilter createEntity(long id, Object command) {
        return BizFilter.create(id, (CreateBizFilterCommand) command);
    }
}
