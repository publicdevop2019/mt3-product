package com.mt.mall.domain.model.catalog;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
import com.mt.common.validate.HttpValidationNotificationHandler;
import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.common.validate.Validator;
import com.mt.mall.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
        HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        validate(handler);
        DomainRegistry.getCatalogValidationService().validate(attributes, handler);
    }

    public void replace(String name, CatalogId parentId, Set<String> attributes, Type catalogType) {
        setName(name);
        setParentId(parentId);
        setAttributes(attributes);
        setType(catalogType);
        HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        validate(handler);
        DomainRegistry.getCatalogValidationService().validate(attributes, handler);
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new CatalogValidator(this, handler)).validate();
    }

    public void setAttributes(Set<String> attributes) {
        Validator.notEmpty(attributes);
        this.attributes = attributes;
    }

    public void setName(String name) {
        Validator.whitelistOnly(name);
        Validator.lengthLessThanOrEqualTo(name, 50);
        Validator.notBlank(name);
        this.name = name;
    }
}
