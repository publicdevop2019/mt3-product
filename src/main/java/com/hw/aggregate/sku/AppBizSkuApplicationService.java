package com.hw.aggregate.sku;

import com.hw.aggregate.sku.command.AppCreateBizSkuCommand;
import com.hw.aggregate.sku.command.AppUpdateBizSkuCommand;
import com.hw.aggregate.sku.model.BizSku;
import com.hw.aggregate.sku.representation.AppBizSkuCardRep;
import com.hw.aggregate.sku.representation.AppBizSkuRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

@Service
public class AppBizSkuApplicationService extends RoleBasedRestfulService<BizSku, AppBizSkuCardRep, AppBizSkuRep, VoidTypedClass> {
    {
        entityClass = BizSku.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public BizSku replaceEntity(BizSku bizSku, Object command) {
        return bizSku.replace((AppUpdateBizSkuCommand) command);
    }

    @Override
    public AppBizSkuCardRep getEntitySumRepresentation(BizSku bizSku) {
        return new AppBizSkuCardRep(bizSku);
    }

    @Override
    public AppBizSkuRep getEntityRepresentation(BizSku bizSku) {
        return new AppBizSkuRep(bizSku);
    }

    @Override
    protected BizSku createEntity(long id, Object command) {
        return BizSku.create(id, (AppCreateBizSkuCommand) command);
    }
}
