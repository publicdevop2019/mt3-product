package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.BizAttributeManager;
import com.hw.aggregate.attribute.representation.AppBizAttributeCardRep;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AppBizAttributeApplicationService extends DefaultRoleBasedRestfulService<BizAttribute, AppBizAttributeCardRep, Void, VoidTypedClass> {
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
        role = RestfulEntityManager.RoleEnum.APP;
    }

    @Override
    public BizAttribute replaceEntity(BizAttribute bizAttribute, Object command) {
        return null;
    }

    @Override
    public AppBizAttributeCardRep getEntitySumRepresentation(BizAttribute bizAttribute) {
        return new AppBizAttributeCardRep(bizAttribute);
    }

    @Override
    public Void getEntityRepresentation(BizAttribute bizAttribute) {
        return null;
    }

    @Override
    public CreatedRep getCreatedEntityRepresentation(BizAttribute created) {
        return null;
    }

    @Override
    protected BizAttribute createEntity(long id, Object command) {
        return null;
    }
}