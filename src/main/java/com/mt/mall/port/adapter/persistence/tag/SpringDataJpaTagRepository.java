package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SqlSelectQueryConverter;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldStringEqualClause;
import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.TagQuery;
import com.mt.mall.domain.model.tag.TagRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;

public interface SpringDataJpaTagRepository extends TagRepository, JpaRepository<Tag, Long> {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default TagId nextIdentity() {
        return new TagId();
    }

    default Optional<Tag> tagOfId(TagId tagOfId) {
        return getTagOfId(tagOfId);
    }

    private Optional<Tag> getTagOfId(TagId tagId) {
        SqlSelectQueryConverter<Tag> tagSelectQueryBuilder = QueryBuilderRegistry.tagSelectQueryBuilder();
        List<Tag> select = tagSelectQueryBuilder.select(new TagQuery(tagId), new PageConfig(), Tag.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
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

    default SumPagedRep<Tag> tagsOfQuery(TagQuery query, PageConfig pageConfig, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.tagSelectQueryBuilder(), query, pageConfig, queryConfig, Tag.class);
    }

    default SumPagedRep<Tag> tagsOfQuery(TagQuery query, PageConfig pageConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.tagSelectQueryBuilder(), query, pageConfig, new QueryConfig(), Tag.class);
    }

    @Component
    class TagQueryBuilder extends SqlSelectQueryConverter<Tag> {
        public transient static final String NAME_LITERAL = "name";
        public transient static final String TAG_ID_LITERAL = "tagId";
        public transient static final String TYPE_LITERAL = "type";

        {
            supportedSort.put("id", TAG_ID_LITERAL);
            supportedSort.put(NAME_LITERAL, NAME_LITERAL);
            supportedSort.put(TYPE_LITERAL, TYPE_LITERAL);
            supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(TAG_ID_LITERAL));
            supportedWhere.put(NAME_LITERAL, new FieldStringLikeClause<>(NAME_LITERAL));
            supportedWhere.put(TYPE_LITERAL, new FieldStringEqualClause<>(TYPE_LITERAL));
        }
    }
}
