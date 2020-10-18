package com.hw.aggregate.catalog;

import com.hw.aggregate.catalog.model.BizCatalog;
import com.hw.aggregate.catalog.representation.PublicBizCatalogCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class PublicBizCatalogApplicationService extends DefaultRoleBasedRestfulService<BizCatalog, PublicBizCatalogCardRep, Void, VoidTypedClass> {

    @PostConstruct
    private void setUp() {
        entityClass = BizCatalog.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
    }

    @Override
    public BizCatalog replaceEntity(BizCatalog catalog, Object command) {
        return null;
    }

    @Override
    public PublicBizCatalogCardRep getEntitySumRepresentation(BizCatalog catalog) {
        return new PublicBizCatalogCardRep(catalog);
    }

    @Override
    public Void getEntityRepresentation(BizCatalog catalog) {
        return null;
    }

    @Override
    protected BizCatalog createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(BizCatalog bizCatalog) {

    }

    @Override
    public void postDelete(BizCatalog bizCatalog) {

    }

    @Override
    protected void prePatch(BizCatalog bizCatalog, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(BizCatalog bizCatalog, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}
