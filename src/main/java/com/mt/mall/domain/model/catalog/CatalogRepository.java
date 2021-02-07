package com.mt.mall.domain.model.catalog;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.catalog.CatalogQuery;

import java.util.Optional;
import java.util.Set;

public interface CatalogRepository {
    SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery catalogQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Catalog> catalogOfId(CatalogId catalogId);

    void add(Catalog catalog);

    SumPagedRep<Catalog> catalogsOfQuery(CatalogQuery queryParam, DefaultPaging queryPagingParam);

    void remove(Set<Catalog> allClientsOfQuery);

    void remove(Catalog catalog);

    CatalogId nextIdentity();

}
