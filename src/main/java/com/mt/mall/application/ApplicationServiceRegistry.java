package com.mt.mall.application;

import com.mt.common.idempotent.ApplicationServiceIdempotentWrapper;
import com.mt.mall.application.catalog.CatalogApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static CatalogApplicationService catalogApplicationService;
    private static ApplicationServiceIdempotentWrapper idempotentWrapper;

    public static CatalogApplicationService catalogApplicationService() {
        return catalogApplicationService;
    }

    public static ApplicationServiceIdempotentWrapper idempotentWrapper() {
        return idempotentWrapper;
    }

    @Autowired
    public void setCatalogApplicationService(CatalogApplicationService catalogApplicationService) {
        ApplicationServiceRegistry.catalogApplicationService = catalogApplicationService;
    }

    @Autowired
    public void setApplicationServiceIdempotentWrapper(ApplicationServiceIdempotentWrapper idempotentWrapper) {
        ApplicationServiceRegistry.idempotentWrapper = idempotentWrapper;
    }
}
