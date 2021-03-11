package com.mt.mall.port.adapter.persistence.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.meta.Meta;
import com.mt.mall.domain.model.meta.MetaQuery;
import com.mt.mall.domain.model.meta.MetaRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaMetaRepository extends MetaRepository {
    default SumPagedRep<Meta> metaOfQuery(MetaQuery query) {
        return QueryBuilderRegistry.getMetaAdaptor().execute(query);
    }

    class JpaCriteriaApiMetaAdaptor {
        public SumPagedRep<Meta> execute(MetaQuery query) {
            QueryUtility.QueryContext<Meta> context = QueryUtility.prepareContext(Meta.class, query);
            QueryUtility.addDomainIdInPredicate(query.getDomainIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), "domainId", context);
            return QueryUtility.pagedQuery(query, context);
        }
    }
}
