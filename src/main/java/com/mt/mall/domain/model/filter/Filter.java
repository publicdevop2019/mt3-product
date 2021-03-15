package com.mt.mall.domain.model.filter;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.infrastructure.FilterCatalogsConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "filter_")
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Filter extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Convert(converter = FilterCatalogsConverter.class)
    private Set<CatalogId> catalogs;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "filterId", unique = true, updatable = false, nullable = false))
    })
    private FilterId filterId;

    private String description;

    @Convert(converter = FilterItem.FilterItemConverter.class)
    private Set<FilterItem> filterItems;

    private void setCatalogs(Set<CatalogId> catalogs) {
        Validator.notEmpty(catalogs);
        DomainRegistry.getFilterValidationService().validateCatalogs(catalogs, new HttpValidationNotificationHandler());
        this.catalogs = catalogs;
    }

    private void setDescription(String description) {
        Validator.whitelistOnly(description);
        Validator.notBlank(description);
        Validator.lengthLessThanOrEqualTo(description, 50);
        this.description = description;
    }

    private void setFilterItems(Set<FilterItem> filterItems) {
        Validator.notEmpty(filterItems);
        DomainRegistry.getFilterValidationService().validateTags(filterItems, new HttpValidationNotificationHandler());
        this.filterItems = filterItems;
    }

    public void replace(Set<CatalogId> catalogs, Set<FilterItem> filterItems, String description) {
        setCatalogs(catalogs);
        setFilterItems(filterItems);
        setDescription(description);
    }

    public Filter(FilterId filterId, Set<CatalogId> catalogs, Set<FilterItem> filterItems, String description) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setCatalogs(catalogs);
        setFilterItems(filterItems);
        setDescription(description);
        setFilterId(filterId);
    }

    public void replace(Set<CatalogId> catalogs, String description) {
        setCatalogs(catalogs);
        setDescription(description);
    }
}
