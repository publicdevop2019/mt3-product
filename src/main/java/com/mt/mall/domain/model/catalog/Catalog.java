package com.mt.mall.domain.model.catalog;

import com.mt.common.audit.Auditable;
import com.mt.common.persistence.EnumConverter;
import com.mt.common.persistence.StringSetConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "catalog_")
@NoArgsConstructor
@Getter
public class Catalog extends Auditable {

    @Id
    private Long id;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private String name;

    @Setter(AccessLevel.PRIVATE)
    private CatalogId parentId;

    @Setter(AccessLevel.PRIVATE)
    private CatalogId catalogId;


    @Convert(converter = StringSetConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Set<String> attributes;

    @Convert(converter = CatalogType.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private CatalogType type;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public Catalog(CatalogId catalogId, String name, CatalogId parentId, Set<String> attributes, CatalogType catalogType) {
        setCatalogId(catalogId);
        setName(name);
        setParentId(parentId);
        setAttributes(attributes);
        setType(catalogType);
    }

    public void replace(String name, CatalogId parentId, Set<String> attributes, CatalogType catalogType) {
        this.setName(name);
        this.setParentId(parentId);
        this.setAttributes(attributes);
        this.setType(catalogType);
    }


    public enum CatalogType {
        FRONTEND,
        BACKEND,
        ;

        public static class DBConverter extends EnumConverter<CatalogType> {
            public DBConverter() {
                super(CatalogType.class);
            }
        }
    }
}
