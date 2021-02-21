package com.mt.mall.domain.model.filter;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
import com.mt.common.validate.HttpValidationNotificationHandler;
import com.mt.common.validate.Validator;
import com.mt.mall.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@Table(name = "filter_")
@NoArgsConstructor
public class Filter extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Convert(converter = StringSetConverter.class)
    private Set<String> catalogs;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "filterId", updatable = false, nullable = false))
    })
    private FilterId filterId;

    private String description;

    @Convert(converter = FilterItem.FilterItemConverter.class)
    private Set<FilterItem> filterItems;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;


    private void setCatalogs(Set<String> catalogs) {
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

    public void replace(Set<String> catalogs, Set<FilterItem> filterItems, String description) {
        setCatalogs(catalogs);
        setFilterItems(filterItems);
        setDescription(description);
    }

    public Filter(FilterId filterId, Set<String> catalogs, Set<FilterItem> filterItems, String description) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
        setCatalogs(catalogs);
        setFilterItems(filterItems);
        setDescription(description);
        setFilterId(filterId);
    }

    public void replace(Set<String> catalogs, String description) {
        setCatalogs(catalogs);
        setDescription(description);
    }
}
