package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.mall.domain.model.tag.TagId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@NoArgsConstructor
@Getter
public class Meta {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Embedded
    private DomainId domainId;
    @Column(updatable = false)
    private MetaType type;
    private Boolean hasChangedTag;
    private Set<TagId> changedTagId;

    public enum MetaType {
        CATALOG,
        FILTER,
        PRODUCT,
        SKU,
    }

    public Meta(DomainId domainId, MetaType type, Boolean hasChangedTag, Set<TagId> changedTagId) {
        this.domainId = domainId;
        this.type = type;
        this.hasChangedTag = hasChangedTag;
        this.changedTagId = changedTagId;
    }

    public void addChangeTag(TagId tagId) {
        if (changedTagId == null)
            changedTagId = new HashSet<>();
        changedTagId.add(tagId);
    }
}
