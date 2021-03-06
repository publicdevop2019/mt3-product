package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;


@Getter
public class CatalogQuery extends QueryCriteria {
    private static final String TYPE_LITERAL = "type";
    private static final String PARENT_ID_LITERAL = "parentId";
    @Setter(AccessLevel.PRIVATE)
    private Set<CatalogId> catalogIds;
    @Setter(AccessLevel.PRIVATE)
    private CatalogId parentId;
    @Setter(AccessLevel.PRIVATE)
    private Type type;
    private CatalogSort catalogSort;

    public CatalogQuery(String query, String pageConfig, String queryConfig) {
        setPageConfig(new PageConfig(pageConfig, 2000));
        setQueryConfig(new QueryConfig(queryConfig));
        setQueryDetail(QueryUtility.parseQuery(query));
    }

    public CatalogQuery(String query) {
        setPageConfig(new PageConfig());
        setQueryConfig(new QueryConfig());
        setQueryDetail(QueryUtility.parseQuery(query));
    }

    private void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
        setCatalogSort();
    }

    private void setQueryDetail(Map<String, String> queryMap) {
        if (queryMap.get("id") != null) {
            String id = queryMap.get("id");
            setCatalogIds(Arrays.stream(id.split("\\.")).map(CatalogId::new).collect(Collectors.toSet()));
        }
        if (queryMap.get(TYPE_LITERAL) != null) {
            String type = queryMap.get(TYPE_LITERAL);
            if (type.equalsIgnoreCase("frontend")) {
                setType(Type.FRONTEND);
            } else if (type.equalsIgnoreCase("backend")) {
                setType(Type.BACKEND);
            } else {
                throw new IllegalArgumentException("unable to parse string type enum");
            }
        }
        if (queryMap.get(PARENT_ID_LITERAL) != null) {
            String parentId = queryMap.get(PARENT_ID_LITERAL);
            setParentId(new CatalogId(parentId));
        }
    }

    private void setCatalogSort() {
        if (pageConfig.getSortBy().equalsIgnoreCase("name"))
            this.catalogSort = CatalogSort.sortByName(pageConfig.isSortOrderAsc());
        if (pageConfig.getSortBy().equalsIgnoreCase("id"))
            this.catalogSort = CatalogSort.sortByCatalogId(pageConfig.isSortOrderAsc());
    }

    private CatalogQuery() {
    }

    public CatalogQuery(CatalogId catalogId) {
        this.catalogIds = new HashSet<>(List.of(catalogId));
        setQueryConfig(QueryConfig.skipCount());
        setPageConfig(PageConfig.defaultConfig());
    }

    public CatalogQuery(Set<CatalogId> catalogIds) {
        this.catalogIds = catalogIds;
    }

    public static CatalogQuery publicQuery(String pageConfig, String queryConfig) {
        CatalogQuery catalogQuery = new CatalogQuery();
        catalogQuery.setPageConfig(new PageConfig(pageConfig, 1500));
        catalogQuery.setType(Type.FRONTEND);
        catalogQuery.setQueryConfig(new QueryConfig(queryConfig));
        return catalogQuery;
    }

    @Getter
    public static class CatalogSort {
        private boolean byName;
        private boolean byId;
        private final boolean isAscending;

        private void setByName() {
            this.byName = true;
        }

        private void setById() {
            this.byId = true;
        }

        public CatalogSort(boolean isAscending) {
            this.isAscending = isAscending;
        }

        public static CatalogSort sortByName(boolean isAscending) {
            CatalogSort catalogSort = new CatalogSort(isAscending);
            catalogSort.setByName();
            return catalogSort;
        }

        public static CatalogSort sortByCatalogId(boolean isAscending) {
            CatalogSort catalogSort = new CatalogSort(isAscending);
            catalogSort.setById();
            return catalogSort;
        }
    }
}
