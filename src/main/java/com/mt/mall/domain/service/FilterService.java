package com.mt.mall.domain.service;

import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.filter.FilterQuery;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class FilterService {
    public Set<Filter> getFiltersOfQuery(FilterQuery queryParam) {
        DefaultPaging queryPagingParam = new DefaultPaging();
        SumPagedRep<Filter> tSumPagedRep = DomainRegistry.filterRepository().filtersOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new HashSet<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<Filter> data = new HashSet<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.filterRepository().filtersOfQuery(queryParam, queryPagingParam.pageOf(a)).getData());
        }
        return data;
    }

    public FilterId create(FilterId filterId, String des, Set<String> catalogs, Set<FilterItem> filter1) {
        Filter filter = new Filter(filterId, catalogs, filter1, des);
        return filter.getFilterId();
    }
}
