package com.mt.mall.domain.model.filter;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
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
    public transient static final String ID_LITERAL = "id";
    public transient static final String ENTITY_CATALOG_LITERAL = "catalogs";
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Convert(converter = StringSetConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Set<String> catalogs;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "filterId", updatable = false, nullable = false))
    })
    private FilterId filterId;

    @Setter(AccessLevel.PRIVATE)
    private String description;

    @Setter(AccessLevel.PRIVATE)
    @Convert(converter = FilterItem.FilterItemConverter.class)
    private Set<FilterItem> filterItems;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

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
