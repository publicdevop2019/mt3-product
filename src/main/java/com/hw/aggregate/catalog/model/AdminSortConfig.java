package com.hw.aggregate.catalog.model;

import com.hw.shared.MaxPageSizeExceedException;
import com.hw.shared.SortOrder;
import com.hw.shared.UnSupportedSortConfigException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public enum AdminSortConfig {
    id("id"),
    type("type"),
    name("name");
    public static final Integer DEFAULT_PAGE_SIZE = 40;
    public static final Integer MAX_PAGE_SIZE = 2000;
    public static final Integer DEFAULT_PAGE_NUM = 0;
    public static final AdminSortConfig DEFAULT_SORT_BY = id;
    public static final Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;

    private final String mappedField;

    AdminSortConfig(String mappedField) {
        this.mappedField = mappedField;
    }

    public static AdminSortConfig fromString(String text) {
        for (AdminSortConfig b : AdminSortConfig.values()) {
            if (b.mappedField.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new UnSupportedSortConfigException();
    }

    public static PageRequest getPageRequestAdmin(Integer pageNumber, Integer pageSize, AdminSortConfig sortBy, SortOrder sortOrder) {
        Sort sort;
        if (pageNumber == null)
            pageNumber = AdminSortConfig.DEFAULT_PAGE_NUM;
        if (sortBy == null)
            sortBy = AdminSortConfig.DEFAULT_SORT_BY;
        if (pageSize == null)
            pageSize = AdminSortConfig.DEFAULT_PAGE_SIZE;
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        if (sortOrder == null) {
            sort = new Sort(AdminSortConfig.DEFAULT_SORT_ORDER, sortBy.mappedField);
        } else {
            switch (sortOrder) {
                case asc: {
                    sort = new Sort(Sort.Direction.ASC, sortBy.mappedField);
                    break;
                }
                case desc: {
                    sort = new Sort(Sort.Direction.DESC, sortBy.mappedField);
                    break;
                }
                default: {
                    sort = new Sort(AdminSortConfig.DEFAULT_SORT_ORDER, sortBy.mappedField);
                }
            }
        }
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
