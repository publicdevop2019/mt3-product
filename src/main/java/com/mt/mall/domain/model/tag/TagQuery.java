package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class TagQuery extends QueryCriteria {
    private Set<TagId> tagIds;
    private String name;
    private Type type;
    private TagSort tagSort;

    public TagQuery(String queryParam) {

    }

    public TagQuery(TagId tagId) {
        tagIds = new HashSet<>(List.of(tagId));
    }

    public TagQuery(Set<TagId> collect) {
        tagIds = collect;
    }

    public TagSort getTagSort() {
        return tagSort;
    }

    @Getter
    public static class TagSort {
        private boolean isById;
        private boolean isByName;
        private boolean isByType;
        private boolean isAsc;

        public boolean isAsc() {
            return isAsc;
        }
    }
}
