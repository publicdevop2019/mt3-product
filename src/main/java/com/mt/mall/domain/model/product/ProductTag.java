package com.mt.mall.domain.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.mt.common.domain.model.validate.Validator;
import com.mt.mall.domain.model.tag.TagId;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Table
@NoArgsConstructor
public class ProductTag implements Serializable {
    @Id
    private Long id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "tagId", updatable = false, nullable = false))
    })
    private TagId tagId;
    private String tagValue;
    @Convert(converter = TagType.DBConverter.class)
    private TagType type;
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private final Set<Product> products = new HashSet<>();

    public ProductTag(Long id, String raw, TagType type) {
        this.id = id;
        this.type = type;
        String[] split = raw.split(":");
        setTagId(split[0]);
        setTagValue(split[1]);
    }

    private void setTagId(String tagId) {
        Validator.notBlank(tagId);
        this.tagId = new TagId(tagId);
    }

    private void setTagValue(String tagValue) {
        Validator.notBlank(tagValue);
        this.tagValue = tagValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTag)) return false;
        ProductTag that = (ProductTag) o;
        return Objects.equal(tagId, that.tagId) && Objects.equal(tagValue, that.tagValue) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagId, tagValue, type);
    }
}
