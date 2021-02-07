package com.mt.mall.domain;

import com.mt.mall.domain.model.catalog.CatalogRepository;
import com.mt.mall.domain.model.filter.FilterRepository;
import com.mt.mall.domain.service.CatalogService;
import com.mt.mall.domain.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static CatalogRepository catalogRepository;
    private static FilterRepository filterRepository;
    private static CatalogService catalogService;
    private static FilterService filterService;

    public static CatalogRepository catalogRepository() {
        return catalogRepository;
    }

    public static FilterService filterService() {
        return filterService;
    }

    public static FilterRepository filterRepository() {
        return filterRepository;
    }


    @Autowired
    private void setCatalogRepository(CatalogRepository catalogRepository) {
        DomainRegistry.catalogRepository = catalogRepository;
    }

    @Autowired
    private void setFilterService(FilterService filterService) {
        DomainRegistry.filterService = filterService;
    }

    @Autowired
    private void setFilterRepository(FilterRepository filterRepository) {
        DomainRegistry.filterRepository = filterRepository;
    }


    public static CatalogService catalogService() {
        return catalogService;
    }

    @Autowired
    private void setCatalogService(CatalogService catalogService) {
        DomainRegistry.catalogService = catalogService;
    }
}
