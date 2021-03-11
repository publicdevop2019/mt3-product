package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.mall.domain.model.tag.TagId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
@NoArgsConstructor
@Getter
public class Meta {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private DomainId domainId;
    private Type type;
    private Boolean hasChangedTag;
    private TagId changedTagId;

    private enum Type {
        CATALOG,
        FILTER,
        PRODUCT,
        SKU,
    }
}
