package com.mt.mall.domain.model.filter.event;

import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.filter.FilterId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FilterUpdated extends FilterEvent{
    public FilterUpdated(FilterId filterId) {
        super(filterId);
    }
}
