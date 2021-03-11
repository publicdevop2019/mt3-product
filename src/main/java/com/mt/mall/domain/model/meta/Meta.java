package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.mall.domain.model.tag.TagId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
@NoArgsConstructor
@Getter
public class Meta {
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
