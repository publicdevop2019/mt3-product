package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TagQuery extends QueryCriteria {
    private Set<TagId> tagIds;
    private String name;
    private Type type;
    private TagSort tagSort;

    public TagQuery(String queryParam) {
        updateQueryParam(queryParam);
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig(PageConfig.defaultConfig());
        setTagSort(pageConfig);

    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        name = stringStringMap.get("name");
        Optional.ofNullable(stringStringMap.get("type")).ifPresent(e -> {
            if (e.equalsIgnoreCase(Type.GEN_ATTR.name())) {
                type = Type.GEN_ATTR;
            }
            if (e.equalsIgnoreCase(Type.KEY_ATTR.name())) {
                type = Type.KEY_ATTR;
            }
            if (e.equalsIgnoreCase(Type.PROD_ATTR.name())) {
                type = Type.PROD_ATTR;
            }
            if (e.equalsIgnoreCase(Type.SALES_ATTR.name())) {
                type = Type.SALES_ATTR;
            }
        });
        Optional.ofNullable(stringStringMap.get("id")).ifPresent(e -> {
            tagIds = Arrays.stream(e.split("\\.")).map(TagId::new).collect(Collectors.toSet());
        });
    }

    private void setTagSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.tagSort = TagSort.byId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("name")) {
            this.tagSort = TagSort.byName(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("type")) {
            this.tagSort = TagSort.byType(pageConfig.isSortOrderAsc());
        }
    }

    public TagQuery(TagId tagId) {
        tagIds = new HashSet<>(List.of(tagId));
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig(PageConfig.defaultConfig());
        setTagSort(pageConfig);
    }

    public TagQuery(Set<TagId> collect) {
        tagIds = collect;
        setQueryConfig(QueryConfig.countRequired());
        setPageConfig(PageConfig.defaultConfig());
        setTagSort(pageConfig);
    }

    public TagQuery(String queryParam, String pageParam, String skipCount) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(skipCount));
        setTagSort(pageConfig);
    }

    @Getter
    public static class TagSort {
        private boolean isById;
        private boolean isByName;
        private boolean isByType;
        private final boolean isAsc;

        private TagSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static TagSort byId(boolean isAsc) {
            TagSort tagSort = new TagSort(isAsc);
            tagSort.isById = true;
            return tagSort;
        }

        public static TagSort byName(boolean isAsc) {
            TagSort tagSort = new TagSort(isAsc);
            tagSort.isByName = true;
            return tagSort;
        }

        public static TagSort byType(boolean isAsc) {
            TagSort tagSort = new TagSort(isAsc);
            tagSort.isByType = true;
            return tagSort;
        }
    }
}
