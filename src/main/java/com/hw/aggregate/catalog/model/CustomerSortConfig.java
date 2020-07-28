package com.hw.aggregate.catalog.model;

import com.hw.shared.MaxPageSizeExceedException;
import com.hw.shared.SortOrder;
import com.hw.shared.UnSupportedSortConfigException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public enum CustomerSortConfig {
    id("id"),
    ;
    public static final Integer DEFAULT_PAGE_SIZE = 1000;
    public static final Integer DEFAULT_PAGE_NUM = 0;
    public static final Integer MAX_PAGE_SIZE = 1500;
    public static final CustomerSortConfig DEFAULT_SORT_BY = id;
    public static final Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;
    private final String mappedField;

    CustomerSortConfig(String mappedField) {
        this.mappedField = mappedField;
    }

    public static CustomerSortConfig fromString(String text) {
        for (CustomerSortConfig b : CustomerSortConfig.values()) {
            if (b.mappedField.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new UnSupportedSortConfigException();
    }

    public static PageRequest getPageRequestCustomer(Integer pageNumber, Integer pageSize, CustomerSortConfig sortBy, SortOrder sort) {
        Sort orders;
        if (pageNumber == null)
            pageNumber = CustomerSortConfig.DEFAULT_PAGE_NUM;
        if (sortBy == null)
            sortBy = CustomerSortConfig.DEFAULT_SORT_BY;
        if (pageSize == null)
            pageSize = CustomerSortConfig.DEFAULT_PAGE_SIZE;
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        if (sort == null) {
            orders = new Sort(CustomerSortConfig.DEFAULT_SORT_ORDER, sortBy.mappedField);
        } else {
            switch (sort) {
                case asc: {
                    orders = new Sort(Sort.Direction.ASC, sortBy.mappedField);
                    break;
                }
                case desc: {
                    orders = new Sort(Sort.Direction.DESC, sortBy.mappedField);
                    break;
                }
                default: {
                    orders = new Sort(CustomerSortConfig.DEFAULT_SORT_ORDER, sortBy.mappedField);
                }
            }
        }
        return PageRequest.of(pageNumber, pageSize, orders);
    }
}
