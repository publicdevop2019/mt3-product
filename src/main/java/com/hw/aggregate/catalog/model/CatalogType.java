package com.hw.aggregate.catalog.model;

import com.hw.shared.EnumDBConverter;

public enum CatalogType {
    FRONTEND,
    BACKEND,
    ;

    public static class DBConverter extends EnumDBConverter {
        public DBConverter() {
            super(CatalogType.class);
        }
    }
}
