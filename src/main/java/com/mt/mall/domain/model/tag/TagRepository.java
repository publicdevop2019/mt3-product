package com.mt.mall.domain.model.tag;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.tag.TagQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository{
    SumPagedRep<Tag> tagsOfQuery(TagQuery queryParam, DefaultPaging queryPagingParam);

    TagId nextIdentity();

    void add(Tag tag);

    SumPagedRep<Tag> tagsOfQuery(TagQuery tagQuery, DefaultPaging defaultPaging, QueryConfig queryConfig);

    Optional<Tag> tagOfId(TagId tagId);

    void remove(Tag tag);

    void remove(Set<Tag> tags);
}
