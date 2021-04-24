package com.mt.mall.application;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.event.CatalogUpdated;
import com.mt.mall.domain.model.filter.event.FilterUpdated;
import com.mt.mall.domain.model.meta.Meta;
import com.mt.mall.domain.model.meta.MetaQuery;
import com.mt.mall.domain.model.product.event.ProductUpdated;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.event.TagCriticalFieldChanged;
import com.mt.mall.domain.model.tag.event.TagDeleted;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class MetaApplicationService {
    @SubscribeForEvent
    @Transactional
    public void handleChange(StoredEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(event.getId().toString(), (ignored) -> {
            if (TagDeleted.class.getName().equals(event.getName())) {
                TagDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), TagDeleted.class);
                DomainRegistry.getMetaService().findImpactedEntities(new TagId(deserialize.getDomainId().getDomainId()));
            } else if (TagCriticalFieldChanged.class.getName().equals(event.getName())) {
                TagCriticalFieldChanged deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), TagCriticalFieldChanged.class);
                DomainRegistry.getMetaService().findImpactedEntities(new TagId(deserialize.getDomainId().getDomainId()));
            } else if (CatalogUpdated.class.getName().equals(event.getName())) {
                CatalogUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CatalogUpdated.class);
                updateMetaWarning(deserialize.getDomainId(), deserialize.getTimestamp());
            } else if (ProductUpdated.class.getName().equals(event.getName())) {
                ProductUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProductUpdated.class);
                updateMetaWarning(deserialize.getDomainId(), deserialize.getTimestamp());
            } else if (FilterUpdated.class.getName().equals(event.getName())) {
                FilterUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), FilterUpdated.class);
                updateMetaWarning(deserialize.getDomainId(), deserialize.getTimestamp());
            }
            return null;
        }, "Meta");
    }

    private void updateMetaWarning(DomainId domainId, Long timestamp) {
        SumPagedRep<Meta> metaSumPagedRep = DomainRegistry.getMetaRepository().metaOfQuery(new MetaQuery(Collections.singleton(domainId)));
        Optional<Meta> first = metaSumPagedRep.findFirst();
        first.ifPresent(e -> e.updateWarning(timestamp));
    }
}
