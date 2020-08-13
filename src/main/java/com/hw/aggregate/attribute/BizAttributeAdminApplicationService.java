package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.BizAttributeManager;
import com.hw.aggregate.attribute.representation.BizAttributeAdminRep;
import com.hw.aggregate.attribute.representation.BizAttributeAdminSumRep.BizAttributeCardRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeAdminCreatedRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BizAttributeAdminApplicationService extends DefaultRoleBasedApplicationService<BizAttribute> {
    @Autowired
    private BizAttributeRepository repo2;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private BizAttributeManager bizAttributeManager2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = bizAttributeManager2;
        entityClass = BizAttribute.class;
        role = RestfulEntityManager.RoleEnum.ADMIN;
    }

    @Override
    public BizAttribute replaceEntity(BizAttribute bizAttribute, Object command) {
        return bizAttribute.replace((UpdateBizAttributeCommand) command);
    }

    @Override
    public BizAttributeCardRepresentation getEntitySumRepresentation(BizAttribute bizAttribute) {
        return new BizAttributeCardRepresentation(bizAttribute);
    }

    @Override
    public BizAttributeAdminRep getEntityRepresentation(BizAttribute bizAttribute) {
        return new BizAttributeAdminRep(bizAttribute);
    }

    @Override
    public BizAttributeAdminCreatedRep getCreatedEntityRepresentation(BizAttribute created) {
        return new BizAttributeAdminCreatedRep(created);
    }

    @Override
    protected BizAttribute createEntity(long id, Object command) {
        return BizAttribute.create(idGenerator.getId(), (CreateBizAttributeCommand) command);
    }
}
