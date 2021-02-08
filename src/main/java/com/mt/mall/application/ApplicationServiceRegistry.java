package com.mt.mall.application;

import com.mt.common.idempotent.ApplicationServiceIdempotentWrapper;
import com.mt.mall.application.catalog.CatalogApplicationService;
import com.mt.mall.application.filter.FilterApplicationService;
import com.mt.mall.application.tag.TagApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static CatalogApplicationService catalogApplicationService;
    private static ApplicationServiceIdempotentWrapper idempotentWrapper;
    private static FilterApplicationService filterApplicationService;
    private static TagApplicationService tagApplicationService;

    public static TagApplicationService tagApplicationService() {
        return tagApplicationService;
    }
    public static CatalogApplicationService catalogApplicationService() {
        return catalogApplicationService;
    }

    public static FilterApplicationService filterApplicationService() {
        return filterApplicationService;
    }

    public static ApplicationServiceIdempotentWrapper idempotentWrapper() {
        return idempotentWrapper;
    }

    @Autowired
    public void setTagApplicationService(TagApplicationService tagApplicationService) {
        ApplicationServiceRegistry.tagApplicationService = tagApplicationService;
    }
    @Autowired
    public void setCatalogApplicationService(CatalogApplicationService catalogApplicationService) {
        ApplicationServiceRegistry.catalogApplicationService = catalogApplicationService;
    }

    @Autowired
    public void setFilterApplicationService(FilterApplicationService filterApplicationService) {
        ApplicationServiceRegistry.filterApplicationService = filterApplicationService;
    }

    @Autowired
    public void setApplicationServiceIdempotentWrapper(ApplicationServiceIdempotentWrapper idempotentWrapper) {
        ApplicationServiceRegistry.idempotentWrapper = idempotentWrapper;
    }
}
