package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.AdminCreateBizFilterCommand;
import com.hw.aggregate.filter.command.AdminUpdateBizFilterCommand;
import com.hw.aggregate.filter.model.AdminBizFilterPatchMiddleLayer;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.representation.AdminBizFilterCardRep;
import com.hw.aggregate.filter.representation.AdminBizFilterRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AdminBizFilterApplicationService extends RoleBasedRestfulService<BizFilter, AdminBizFilterCardRep, AdminBizFilterRep, AdminBizFilterPatchMiddleLayer> {
    {
        entityClass = BizFilter.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier= AdminBizFilterPatchMiddleLayer::new;
    }

    @Override
    public BizFilter replaceEntity(BizFilter bizFilter, Object command) {
        bizFilter.replace((AdminUpdateBizFilterCommand) command);
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
        return BizFilter.create(id, (AdminCreateBizFilterCommand) command);
    }

}
