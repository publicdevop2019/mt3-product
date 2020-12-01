package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.representation.PublicBizCatalogCardRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class PublicBizCatalogApplicationService extends RoleBasedRestfulService<BizCatalog, PublicBizCatalogCardRep, Void, VoidTypedClass> {
    {
        entityClass = BizCatalog.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
    }

    @Override
    public PublicBizCatalogCardRep getEntitySumRepresentation(BizCatalog catalog) {
        return new PublicBizCatalogCardRep(catalog);
    }
}
