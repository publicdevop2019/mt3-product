package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.tag.*;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaTagRepository extends TagRepository, JpaRepository<Tag, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Tag> tagOfId(TagId tagOfId) {
        return getTagOfId(tagOfId);
    }

    private Optional<Tag> getTagOfId(TagId tagId) {
        return tagsOfQuery(new TagQuery(tagId)).findFirst();
    }

    default void add(Tag client) {
        save(client);
    }

    default void remove(Tag client) {
        softDelete(client.getId());
    }

    default void remove(Set<Tag> client) {
        softDeleteAll(client.stream().map(Tag::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Tag> tagsOfQuery(TagQuery tagQuery) {
        return QueryBuilderRegistry.getTagSelectQueryBuilder().execute(tagQuery);
    }


    @Component
    class JpaCriteriaApiTagAdaptor {
        public SumPagedRep<Tag> execute(TagQuery tagQuery) {
            QueryUtility.QueryContext<Tag> queryContext = QueryUtility.prepareContext(Tag.class, tagQuery);
            Optional.ofNullable(tagQuery.getTagIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(tagQuery.getTagIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Tag_.TAG_ID, queryContext));
            Optional.ofNullable(tagQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(tagQuery.getName(), Tag_.NAME, queryContext));
            Optional.ofNullable(tagQuery.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(tagQuery.getType().name(), Tag_.TYPE, queryContext));
            Order order = null;
            if (tagQuery.getTagSort().isById())
                order = QueryUtility.getDomainIdOrder(Tag_.TAG_ID, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByName())
                order = QueryUtility.getOrder(Tag_.NAME, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByType())
                order = QueryUtility.getOrder(Tag_.TYPE, queryContext, tagQuery.getTagSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(tagQuery, queryContext);
        }
    }
}
