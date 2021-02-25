package com.mt.mall.domain.model.tag;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum Type {
    KEY_ATTR,
    SALES_ATTR,
    PROD_ATTR,
    GEN_ATTR;

    public static class DBConverter extends EnumConverter {
        public DBConverter() {
            super(Type.class);
        }
    }
}
