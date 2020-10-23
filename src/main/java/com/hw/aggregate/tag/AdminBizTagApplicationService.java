package com.hw.aggregate.tag;

import com.hw.aggregate.tag.command.CreateBizTagCommand;
import com.hw.aggregate.tag.command.UpdateBizTagCommand;
import com.hw.aggregate.tag.model.AdminBizTagPatchMiddleLayer;
import com.hw.aggregate.tag.model.BizTag;
import com.hw.aggregate.tag.representation.AdminBizTagCardRep;
import com.hw.aggregate.tag.representation.AdminBizTagRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AdminBizTagApplicationService extends DefaultRoleBasedRestfulService<BizTag, AdminBizTagCardRep, AdminBizTagRep, AdminBizTagPatchMiddleLayer> {

    @PostConstruct
    private void setUp() {
        entityClass = BizTag.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizTagPatchMiddleLayer::new;
    }

    @Override
    public BizTag replaceEntity(BizTag bizAttribute, Object command) {
        return bizAttribute.replace((UpdateBizTagCommand) command);
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
        return BizTag.create(idGenerator.getId(), (CreateBizTagCommand) command);
    }

    @Override
    public void preDelete(BizTag bizAttribute) {

    }

    @Override
    public void postDelete(BizTag bizAttribute) {

    }

    @Override
    protected void prePatch(BizTag bizAttribute, Map<String, Object> params, AdminBizTagPatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(BizTag bizAttribute, Map<String, Object> params, AdminBizTagPatchMiddleLayer middleLayer) {

    }
}
