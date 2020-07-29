package com.hw.aggregate.product.model;

import com.hw.shared.QueryConfig;
import org.springframework.stereotype.Component;

@Component("productAdmin")
public class AdminQueryConfig extends QueryConfig {
    public enum SortBy implements MappedField {
        id("id"),
        name("name"),
        sales("totalSales"),
        expireDate("endAt");
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
        DEFAULT_PAGE_SIZE = 40;
        MAX_PAGE_SIZE = 400;
        DEFAULT_SORT_BY = SortBy.id;
    }
}
