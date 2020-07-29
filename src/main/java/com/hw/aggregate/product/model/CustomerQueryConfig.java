package com.hw.aggregate.product.model;

import com.hw.shared.QueryConfig;
import org.springframework.stereotype.Component;

@Component("productCustomer")
public class CustomerQueryConfig extends QueryConfig {

    public enum SortBy implements MappedField {
        name("name"),
        price("lowestPrice"),
        sales("totalSales");

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
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
        DEFAULT_SORT_BY = SortBy.name;
    }
}
