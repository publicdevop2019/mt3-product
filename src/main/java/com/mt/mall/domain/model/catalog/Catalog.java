package com.mt.mall.domain.model.catalog;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
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
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private String name;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "parentId"))
    })
    private CatalogId parentId;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "catalogId", updatable = false, nullable = false))
    })
    private CatalogId catalogId;


    @Convert(converter = StringSetConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Set<String> attributes;

    @Convert(converter = Type.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Type type;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public Catalog(CatalogId catalogId, String name, CatalogId parentId, Set<String> attributes, Type catalogType) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
        setCatalogId(catalogId);
        setName(name);
        setParentId(parentId);
        setAttributes(attributes);
        setType(catalogType);
    }

    public void replace(String name, CatalogId parentId, Set<String> attributes, Type catalogType) {
        this.setName(name);
        this.setParentId(parentId);
        this.setAttributes(attributes);
        this.setType(catalogType);
    }


}
