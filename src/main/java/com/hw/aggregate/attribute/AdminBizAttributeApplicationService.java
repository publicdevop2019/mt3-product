package com.hw.aggregate.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminBizAttributePatchMiddleLayer;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.BizAttributeQueryRegistry;
import com.hw.aggregate.attribute.representation.AdminBizAttributeCardRep;
import com.hw.aggregate.attribute.representation.AdminBizAttributeRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AdminBizAttributeApplicationService extends DefaultRoleBasedRestfulService<BizAttribute, AdminBizAttributeCardRep, AdminBizAttributeRep, AdminBizAttributePatchMiddleLayer> {
    @Autowired
    private BizAttributeRepository repo2;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private BizAttributeQueryRegistry bizAttributeManager2;
    @Autowired
    private ObjectMapper om2;
    @Autowired
    private AppChangeRecordApplicationService changeHistoryRepository;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        queryRegistry = bizAttributeManager2;
        entityClass = BizAttribute.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizAttributePatchMiddleLayer::new;
        om = om2;
        appChangeRecordApplicationService = changeHistoryRepository;
    }

    @Override
    public BizAttribute replaceEntity(BizAttribute bizAttribute, Object command) {
        return bizAttribute.replace((UpdateBizAttributeCommand) command);
    }

    @Override
    public AdminBizAttributeCardRep getEntitySumRepresentation(BizAttribute bizAttribute) {
        return new AdminBizAttributeCardRep(bizAttribute);
    }

    @Override
    public AdminBizAttributeRep getEntityRepresentation(BizAttribute bizAttribute) {
        return new AdminBizAttributeRep(bizAttribute);
    }

    @Override
    protected BizAttribute createEntity(long id, Object command) {
        return BizAttribute.create(idGenerator.getId(), (CreateBizAttributeCommand) command);
    }

    @Override
    public void preDelete(BizAttribute bizAttribute) {

    }

    @Override
    public void postDelete(BizAttribute bizAttribute) {

    }

    @Override
    protected void prePatch(BizAttribute bizAttribute, Map<String, Object> params, AdminBizAttributePatchMiddleLayer middleLayer) {

    }

    @Override
    protected void postPatch(BizAttribute bizAttribute, Map<String, Object> params, AdminBizAttributePatchMiddleLayer middleLayer) {

    }
}
