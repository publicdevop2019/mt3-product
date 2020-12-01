package com.hw.aggregate.tag;

import com.hw.aggregate.tag.model.BizTag;
import com.hw.aggregate.tag.representation.AppBizTagCardRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AppBizTagApplicationService extends RoleBasedRestfulService<BizTag, AppBizTagCardRep, Void, VoidTypedClass> {
    {
        entityClass = BizTag.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public AppBizTagCardRep getEntitySumRepresentation(BizTag bizAttribute) {
        return new AppBizTagCardRep(bizAttribute);
    }
}
