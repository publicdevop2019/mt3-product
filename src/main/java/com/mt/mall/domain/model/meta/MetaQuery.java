package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;

import java.util.Set;

@Getter
public class MetaQuery extends QueryCriteria {
    private MetaSort metaSort;
    private Set<DomainId> domainIds;

    public MetaQuery(Set<DomainId> collect) {
        setDomainIds(collect);
        this.pageConfig = PageConfig.defaultConfig();
        setQueryConfig(QueryConfig.countRequired());
        setMetaSort();
    }

    private void setMetaSort() {
        if (pageConfig.getSortBy().equalsIgnoreCase("id"))
            this.metaSort = MetaSort.isById(pageConfig.isSortOrderAsc());
    }

    private void setDomainIds(Set<DomainId> domainIds) {
        this.domainIds = domainIds;
    }

    @Getter
    public static class MetaSort {
        private boolean isById = true;
        private final boolean isAsc;

        public static MetaSort isById(boolean isAsc) {
            MetaSort metaSort = new MetaSort(isAsc);
            metaSort.isById = true;
            return metaSort;
        }

        public MetaSort(boolean isAsc) {
            this.isAsc = isAsc;
        }
    }
}
