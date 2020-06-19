package com.hw.aggregate.catalog.model;

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
