package com.hw.aggregate.product.model;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Table(name = "biz_tag")
@NoArgsConstructor
public class Tag {
    @Id
    private Long id;
    private String value;
    @Convert(converter = TagTypeEnum.DBConverter.class)
    private TagTypeEnum type;
    @ManyToMany(mappedBy = "tags")
    private Set<Product> products = new HashSet<>();

    public Tag(Long id, String value, TagTypeEnum type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
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
