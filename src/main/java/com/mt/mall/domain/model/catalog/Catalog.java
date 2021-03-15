package com.mt.mall.domain.model.catalog;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "catalog_")
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Catalog extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "parentId"))
    })
    private CatalogId parentId;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "catalogId", unique = true, updatable = false, nullable = false))
    })
    private CatalogId catalogId;


    @Convert(converter = LinkedTag.LinkedTagConverter.class)
    private Set<LinkedTag> linkedTags;

    @Convert(converter = Type.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Type type;

    private void setParentId(@Nullable CatalogId parentId) {
        if (parentId == null) {
            this.parentId = null;
            return;
        }
        if (parentId.getDomainId() != null)
            this.parentId = parentId;
    }

    public Catalog(CatalogId catalogId, String name, CatalogId parentId, Set<LinkedTag> linkedTags, Type catalogType) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setCatalogId(catalogId);
        setName(name);
        setParentId(parentId);
        setLinkedTags(linkedTags);
        setType(catalogType);
        HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        validate(handler);
        DomainRegistry.getCatalogValidationService().validate(linkedTags, handler);
    }

    public void replace(String name, CatalogId parentId, Set<LinkedTag> linkedTags, Type catalogType) {
        setName(name);
        setParentId(parentId);
        setLinkedTags(linkedTags);
        setType(catalogType);
        HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        validate(handler);
        DomainRegistry.getCatalogValidationService().validate(linkedTags, handler);
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new CatalogValidator(this, handler)).validate();
    }

    private void setLinkedTags(Set<LinkedTag> linkedTags) {
        Validator.notEmpty(linkedTags);
        this.linkedTags = linkedTags;
    }

    private void setName(String name) {
        Validator.whitelistOnly(name);
        Validator.lengthLessThanOrEqualTo(name, 50);
        Validator.notBlank(name);
        this.name = name;
    }
}
