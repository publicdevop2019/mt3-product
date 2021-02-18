package com.mt.mall.port.adapter.persistence.tag;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.TagQuery;
import com.mt.mall.domain.model.tag.TagRepository;
import com.mt.mall.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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

    default TagId nextIdentity() {
        return new TagId();
    }

    default Optional<Tag> tagOfId(TagId tagOfId) {
        return getTagOfId(tagOfId);
    }

    private Optional<Tag> getTagOfId(TagId tagId) {
        SelectQueryBuilder<Tag> tagSelectQueryBuilder = QueryBuilderRegistry.tagSelectQueryBuilder();
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
        return getSumPagedRep(query, pageConfig, queryConfig);
    }

    default SumPagedRep<Tag> tagsOfQuery(TagQuery query, PageConfig pageConfig) {
        return getSumPagedRep(query, pageConfig, new QueryConfig());
    }

    private SumPagedRep<Tag> getSumPagedRep(TagQuery query, PageConfig page, QueryConfig config) {
        SelectQueryBuilder<Tag> selectQueryBuilder = QueryBuilderRegistry.tagSelectQueryBuilder();
        List<Tag> select = selectQueryBuilder.select(query, page, Tag.class);
        Long aLong = null;
        if (config.isSkipCount()) {
            aLong = selectQueryBuilder.count(query, Tag.class);
        }
        return new SumPagedRep<>(select, aLong);
    }
}
