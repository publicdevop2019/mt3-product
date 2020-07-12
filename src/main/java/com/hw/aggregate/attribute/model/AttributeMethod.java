package com.hw.aggregate.attribute.model;


import com.hw.shared.EnumDBConverter;

public enum AttributeMethod {
    MANUAL,
    SELECT;

    public static class DBConverter extends EnumDBConverter {
        public DBConverter() {
            super(AttributeMethod.class);
        }
    }
}
