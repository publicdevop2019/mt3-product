package com.hw.aggregate.attribute.model;

import com.hw.aggregate.catalog.model.EnumDBConverter;

public enum MethodEnum {
    MANUAL,
    SELECT;

    public static class DBConverter extends EnumDBConverter {
        public DBConverter() {
            super(MethodEnum.class);
        }
    }
}
