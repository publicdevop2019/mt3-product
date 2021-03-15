package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;

import java.util.Set;

@Getter
public class MetaQuery extends QueryCriteria {

    private Set<DomainId> domainIds;

    public MetaQuery(Set<DomainId> collect) {
        setDomainIds(collect);
        this.pageConfig = PageConfig.defaultConfig();
        setQueryConfig(QueryConfig.countRequired());
    }

    private void setDomainIds(Set<DomainId> domainIds) {
        this.domainIds = domainIds;
    }
}
