package com.mt.mall.domain.service;

import com.mt.common.query.QueryUtility;
import com.mt.mall.application.filter.FilterQuery;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterItem;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FilterService {
    public FilterId create(FilterId filterId, String des, Set<String> catalogs, Set<FilterItem> filter1) {
        Filter filter = new Filter(filterId, catalogs, filter1, des);
        DomainRegistry.filterRepository().add(filter);
        return filter.getFilterId();
    }
}
