package com.hw.aggregate.tag;

import com.hw.aggregate.tag.model.BizTag;
import com.hw.aggregate.tag.representation.AppBizTagCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AppBizTagApplicationService extends DefaultRoleBasedRestfulService<BizTag, AppBizTagCardRep, Void, VoidTypedClass> {

    @PostConstruct
    private void setUp() {
        entityClass = BizTag.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public BizTag replaceEntity(BizTag bizAttribute, Object command) {
        return null;
    }

    @Override
    public AppBizTagCardRep getEntitySumRepresentation(BizTag bizAttribute) {
        return new AppBizTagCardRep(bizAttribute);
    }

    @Override
    public Void getEntityRepresentation(BizTag bizAttribute) {
        return null;
    }

    @Override
    protected BizTag createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(BizTag bizAttribute) {

    }

    @Override
    public void postDelete(BizTag bizAttribute) {

    }

    @Override
    protected void prePatch(BizTag bizAttribute, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(BizTag bizAttribute, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}
