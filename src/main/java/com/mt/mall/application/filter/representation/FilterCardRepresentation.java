package com.mt.mall.application.filter.representation;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.mall.domain.model.filter.Filter;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FilterCardRepresentation {
    private String id;
    private Set<String> catalogs;
    private String description;
    private Integer version;
    private boolean reviewRequired = false;

    public FilterCardRepresentation(Filter e1,boolean reviewRequired) {
        setId(e1.getFilterId().getDomainId());
        setDescription(e1.getDescription());
        setCatalogs(e1.getCatalogs().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        setVersion(e1.getVersion());
        setReviewRequired(reviewRequired);
    }
}
