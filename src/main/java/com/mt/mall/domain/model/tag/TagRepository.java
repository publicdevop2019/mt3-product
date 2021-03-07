package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;
import java.util.Set;

public interface TagRepository {
    SumPagedRep<Tag> tagsOfQuery(TagQuery tagQuery);

    void add(Tag tag);

    Optional<Tag> tagOfId(TagId tagId);

    void remove(Tag tag);

    void remove(Set<Tag> tags);
}
