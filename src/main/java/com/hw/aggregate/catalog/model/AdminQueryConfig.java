package com.hw.aggregate.catalog.model;

import com.hw.shared.QueryConfig;
import org.springframework.stereotype.Component;

@Component("catalogAdmin")
public class AdminQueryConfig extends QueryConfig {
    public enum SortBy implements MappedField {
        id("id"),
        name("name");
        private final String mappedField;

        SortBy(String mappedField) {
            this.mappedField = mappedField;
        }

        @Override
        public String getMappedField() {
            return this.mappedField;
        }
    }

    AdminQueryConfig() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        DEFAULT_SORT_BY = SortBy.id;
    }
}
