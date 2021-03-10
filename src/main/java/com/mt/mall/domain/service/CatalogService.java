package com.mt.mall.domain.service;

import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.LinkedTag;
import com.mt.mall.domain.model.catalog.Type;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CatalogService {
    public CatalogId create(CatalogId catalogId, String name, CatalogId parentId, Set<LinkedTag> attributes, Type catalogType) {
        Catalog catalog = new Catalog(catalogId, name, parentId, attributes, catalogType);
        DomainRegistry.catalogRepository().add(catalog);
        return catalog.getCatalogId();
    }
}
