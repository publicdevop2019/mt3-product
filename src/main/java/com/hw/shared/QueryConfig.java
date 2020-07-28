package com.hw.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public abstract class QueryConfig {
    public static Integer DEFAULT_PAGE_SIZE;
    public static Integer MAX_PAGE_SIZE;
    public static Integer DEFAULT_PAGE_NUM = 0;
    public static MappedField DEFAULT_SORT_BY;
    public static Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;

    public static PageRequest getPageRequest(Integer pageNumber, Integer pageSize, MappedField sortBy, SortOrder sortOrder) {
        Sort sort;
        if (pageNumber == null)
            pageNumber = DEFAULT_PAGE_NUM;
        if (sortBy == null)
            sortBy = DEFAULT_SORT_BY;
        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        if (sortOrder == null) {
            sort = new Sort(DEFAULT_SORT_ORDER, sortBy.getMappedField());
        } else {
            switch (sortOrder) {
                case asc: {
                    sort = new Sort(Sort.Direction.ASC, sortBy.getMappedField());
                    break;
                }
                case desc: {
                    sort = new Sort(Sort.Direction.DESC, sortBy.getMappedField());
                    break;
                }
                default: {
                    sort = new Sort(DEFAULT_SORT_ORDER, sortBy.getMappedField());
                }
            }
        }
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    public interface MappedField {
        String getMappedField();
    }
}
