package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.AppBizAttributeCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AppBizAttributeApplicationService extends DefaultRoleBasedRestfulService<BizAttribute, AppBizAttributeCardRep, Void, VoidTypedClass> {

    @PostConstruct
    private void setUp() {
        entityClass = BizAttribute.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
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
    protected BizAttribute createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(BizAttribute bizAttribute) {

    }

    @Override
    public void postDelete(BizAttribute bizAttribute) {

    }

    @Override
    protected void prePatch(BizAttribute bizAttribute, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(BizAttribute bizAttribute, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}
