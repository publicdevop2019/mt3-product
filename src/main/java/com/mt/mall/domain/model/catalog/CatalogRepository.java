package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface CatalogRepository {
    SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery catalogQuery);

    Optional<Catalog> catalogOfId(CatalogId catalogId);

    void add(Catalog catalog);

    SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery queryParam, PageConfig queryPagingParam);

    void remove(Set<Catalog> allClientsOfQuery);

    void remove(Catalog catalog);

    CatalogId nextIdentity();

}
