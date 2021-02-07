package com.mt.mall.domain;

import com.mt.mall.domain.model.catalog.CatalogRepository;
import com.mt.mall.domain.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static CatalogRepository catalogRepository;

    public static CatalogRepository catalogRepository() {
        return catalogRepository;
    }


    @Autowired
    private void setCatalogRepository(CatalogRepository catalogRepository) {
        DomainRegistry.catalogRepository = catalogRepository;
    }

    private static CatalogService catalogService;

    public static CatalogService catalogService() {
        return catalogService;
    }

    @Autowired
    private void setCatalogService(CatalogService catalogService) {
        DomainRegistry.catalogService = catalogService;
    }
}
