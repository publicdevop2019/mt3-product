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
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = bizAttributeManager2;
        entityClass = BizAttribute.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizAttributePatchMiddleLayer::new;
        om = om2;
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
}
