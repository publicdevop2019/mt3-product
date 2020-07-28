package com.hw.aggregate.filter.model;

import com.hw.shared.QueryConfig;

public class AdminQueryConfig extends QueryConfig {
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

    static {
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        DEFAULT_SORT_BY = SortBy.id;
    }
}
