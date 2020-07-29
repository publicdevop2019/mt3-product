package com.hw.aggregate.catalog.model;

import com.hw.shared.QueryConfig;
import org.springframework.stereotype.Component;

@Component("catalogCustomer")
public class CustomerQueryConfig extends QueryConfig {
    public enum SortBy implements MappedField {
        id("id");
        private final String mappedField;

        SortBy(String mappedField) {
            this.mappedField = mappedField;
        }

        @Override
        public String getMappedField() {
            return this.mappedField;
        }
    }

    CustomerQueryConfig() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 1500;
        DEFAULT_SORT_BY = SortBy.id;
    }

}
