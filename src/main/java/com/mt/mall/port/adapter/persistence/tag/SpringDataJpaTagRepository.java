package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.TagQuery;
import com.mt.mall.domain.model.tag.TagRepository;
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
        public transient static final String NAME_LITERAL = "name";
        public transient static final String TAG_ID_LITERAL = "tagId";
        public transient static final String TYPE_LITERAL = "type";

        public SumPagedRep<Tag> execute(TagQuery tagQuery) {
            QueryUtility.QueryContext<Tag> queryContext = QueryUtility.prepareContext(Tag.class, tagQuery);
            Optional.ofNullable(tagQuery.getTagIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(tagQuery.getTagIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()), TAG_ID_LITERAL, queryContext));
            Optional.ofNullable(tagQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(tagQuery.getName(), NAME_LITERAL, queryContext));
            Optional.ofNullable(tagQuery.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(tagQuery.getType().name(), TYPE_LITERAL, queryContext));
            Order order = null;
            if (tagQuery.getTagSort().isById())
                order = QueryUtility.getDomainIdOrder(TAG_ID_LITERAL, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByName())
                order = QueryUtility.getOrder(NAME_LITERAL, queryContext, tagQuery.getTagSort().isAsc());
            if (tagQuery.getTagSort().isByType())
                order = QueryUtility.getOrder(TYPE_LITERAL, queryContext, tagQuery.getTagSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(tagQuery, queryContext);
        }
    }
}
