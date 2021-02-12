package com.mt.mall.application.filter.representation;

import com.mt.mall.domain.model.filter.Filter;
import lombok.Data;

import java.util.Set;

@Data
public class FilterCardRepresentation {
    private String id;
    private Set<String> catalogs;
    private String description;
    private Integer version;

    public FilterCardRepresentation(Object e) {
        Filter e1 = (Filter) e;
        setId(e1.getFilterId().getDomainId());
        setDescription(e1.getDescription());
        setCatalogs(e1.getCatalogs());
        setVersion(e1.getVersion());
    }
}
