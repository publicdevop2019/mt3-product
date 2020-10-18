package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminBizFilterPatchMiddleLayer;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.representation.AdminBizFilterCardRep;
import com.hw.aggregate.filter.representation.AdminBizFilterRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AdminBizFilterApplicationService extends DefaultRoleBasedRestfulService<BizFilter, AdminBizFilterCardRep, AdminBizFilterRep, AdminBizFilterPatchMiddleLayer> {
    @PostConstruct
    private void setUp() {
        entityClass = BizFilter.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier= AdminBizFilterPatchMiddleLayer::new;
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

    @Override
    public void preDelete(BizFilter bizFilter) {

    }

    @Override
    public void postDelete(BizFilter bizFilter) {

    }

    @Override
    protected void prePatch(BizFilter bizFilter, Map<String, Object> params, AdminBizFilterPatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(BizFilter bizFilter, Map<String, Object> params, AdminBizFilterPatchMiddleLayer middleLayer) {

    }
}
