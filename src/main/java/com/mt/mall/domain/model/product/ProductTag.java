package com.mt.mall.domain.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
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
    private String value;
    @Convert(converter = TagType.DBConverter.class)
    private TagType type;
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private final Set<Product> products = new HashSet<>();

    public ProductTag(Long id, String value, TagType type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductTag tag = (ProductTag) o;
        return Objects.equal(value, tag.value) && Objects.equal(id, tag.id) && Objects.equal(type, tag.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, id, type);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
