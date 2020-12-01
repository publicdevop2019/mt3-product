package com.hw.aggregate.tag;

import com.hw.aggregate.tag.command.AdminCreateBizTagCommand;
import com.hw.aggregate.tag.command.AdminUpdateBizTagCommand;
import com.hw.aggregate.tag.model.AdminBizTagPatchMiddleLayer;
import com.hw.aggregate.tag.model.BizTag;
import com.hw.aggregate.tag.representation.AdminBizTagCardRep;
import com.hw.aggregate.tag.representation.AdminBizTagRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AdminBizTagApplicationService extends RoleBasedRestfulService<BizTag, AdminBizTagCardRep, AdminBizTagRep, AdminBizTagPatchMiddleLayer> {
    {
        entityClass = BizTag.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizTagPatchMiddleLayer::new;
    }

    @Override
    public BizTag replaceEntity(BizTag bizAttribute, Object command) {
        return bizAttribute.replace((AdminUpdateBizTagCommand) command);
    }

    @Override
    public AdminBizTagCardRep getEntitySumRepresentation(BizTag bizAttribute) {
        return new AdminBizTagCardRep(bizAttribute);
    }

    @Override
    public AdminBizTagRep getEntityRepresentation(BizTag bizAttribute) {
        return new AdminBizTagRep(bizAttribute);
    }

    @Override
    protected BizTag createEntity(long id, Object command) {
        return BizTag.create(idGenerator.getId(), (AdminCreateBizTagCommand) command);
    }

}
