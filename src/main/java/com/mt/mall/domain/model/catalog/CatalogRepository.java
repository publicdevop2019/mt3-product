package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface CatalogRepository {
    SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery catalogQuery);

    Optional<Catalog> catalogOfId(CatalogId catalogId);

    void add(Catalog catalog);

    void remove(Set<Catalog> allClientsOfQuery);

    void remove(Catalog catalog);
}
