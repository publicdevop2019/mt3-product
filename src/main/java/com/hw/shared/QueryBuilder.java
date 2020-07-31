package com.hw.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.Predicate;
import java.util.Map;

public abstract class QueryBuilder {
    public Integer DEFAULT_PAGE_SIZE;
    public Integer MAX_PAGE_SIZE;
    public Integer DEFAULT_PAGE_NUM = 0;
    public String DEFAULT_SORT_BY;
    public Map<String, String> mappedSortBy;
    public Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;

    public abstract Predicate getQueryClause(String search);

    public PageRequest getPageRequest(String page) throws UnsupportedQueryConfigException {
        if (page == null) {
            Sort sort = new Sort(DEFAULT_SORT_ORDER, mappedSortBy.get(DEFAULT_SORT_BY));
            return PageRequest.of(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, sort);
        }
        String[] params = page.split(",");
        Integer pageNumber = DEFAULT_PAGE_NUM;
        Integer pageSize = DEFAULT_PAGE_SIZE;
        String sortBy = mappedSortBy.get(DEFAULT_SORT_BY);
        Sort.Direction sortOrder = DEFAULT_SORT_ORDER;
        for (String param : params) {
            String[] values = param.split(":");
            if (values[0].equals("num") && values[1] != null) {
                pageNumber = Integer.parseInt(values[1]);
            }
            if (values[0].equals("size") && values[1] != null) {
                pageSize = Integer.parseInt(values[1]);
            }
            if (values[0].equals("by") && values[1] != null) {
                sortBy = mappedSortBy.get(values[1]);
                if (sortBy == null)
                    throw new UnsupportedQueryConfigException();
            }
            if (values[0].equals("order") && values[1] != null) {
                sortOrder = Sort.Direction.fromString(values[1]);
            }
        }
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        Sort sort = new Sort(sortOrder, sortBy);
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
