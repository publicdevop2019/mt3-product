package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.mall.domain.model.catalog.CatalogId;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
public class MetaQuery extends QueryCriteria {
    private final Set<DomainId> domainIds;

    public MetaQuery(Set<CatalogId> collect) {
        this.domainIds = collect.stream().map(e -> (DomainId) e).collect(Collectors.toSet());
        this.pageConfig = PageConfig.defaultConfig();
        setQueryConfig(QueryConfig.countRequired());
    }
}
