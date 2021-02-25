package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum TagValueType {
    MANUAL,
    SELECT;

    public static class DBConverter extends EnumConverter {
        public DBConverter() {
            super(TagValueType.class);
        }
    }
}
