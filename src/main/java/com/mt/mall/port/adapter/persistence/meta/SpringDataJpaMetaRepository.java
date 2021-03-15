package com.mt.mall.port.adapter.persistence.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.meta.Meta;
import com.mt.mall.domain.model.meta.MetaQuery;
import com.mt.mall.domain.model.meta.MetaRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Collections;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaMetaRepository extends MetaRepository, JpaRepository<Meta, Long> {
    default void add(Meta meta) {
        save(meta);
    }

    default SumPagedRep<Meta> metaOfQuery(MetaQuery query) {
        return QueryBuilderRegistry.getMetaAdaptor().execute(query);
    }

    @Component
    class JpaCriteriaApiMetaAdaptor {
        public SumPagedRep<Meta> execute(MetaQuery query) {
            if (query.getDomainIds().isEmpty()) {
                return new SumPagedRep<>(Collections.emptyList(), 0L);
            }
            QueryUtility.QueryContext<Meta> context = QueryUtility.prepareContext(Meta.class, query);
            QueryUtility.addDomainIdInPredicate(query.getDomainIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), "domainId", context);
            Order order = null;
            if (query.getMetaSort().isById())
                order = QueryUtility.getOrder("id", context, query.getMetaSort().isAsc());
            context.setOrder(order);
            return QueryUtility.pagedQuery(query, context);
        }
    }
}
