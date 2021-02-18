package com.mt.mall.domain.model.tag;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface TagRepository{
    SumPagedRep<Tag> tagsOfQuery(TagQuery queryParam, PageConfig queryPagingParam);

    TagId nextIdentity();

    void add(Tag tag);

    SumPagedRep<Tag> tagsOfQuery(TagQuery tagQuery, PageConfig defaultPaging, QueryConfig queryConfig);

    Optional<Tag> tagOfId(TagId tagId);

    void remove(Tag tag);

    void remove(Set<Tag> tags);
}
