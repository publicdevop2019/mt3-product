package com.mt.mall.domain.model.catalog.event;

import com.mt.mall.domain.model.catalog.CatalogId;

public class CatalogUpdated extends CatalogEvent{
    public CatalogUpdated(CatalogId catalogId) {
        super(catalogId);
    }
}
