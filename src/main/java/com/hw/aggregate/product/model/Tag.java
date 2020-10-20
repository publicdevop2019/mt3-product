package com.hw.aggregate.product.model;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Table(name = "biz_tag")
@NoArgsConstructor
public class Tag {
    @Id
    private Long id;
    @NaturalId
    private String value;
    @ManyToMany(mappedBy = "tags")
    private Set<Product> products = new HashSet<>();

    public Tag(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equal(value, tag.value) && Objects.equal(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, id);
    }
}
