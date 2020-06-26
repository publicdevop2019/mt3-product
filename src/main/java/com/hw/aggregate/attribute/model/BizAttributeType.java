package com.hw.aggregate.attribute.model;

import com.hw.aggregate.catalog.model.EnumDBConverter;

public enum BizAttributeType {
    KEY_ATTR,
    SALES_ATTR,
    PROD_ATTR,
    GEN_ATTR;
    public static class DBConverter extends EnumDBConverter {
        public DBConverter() {
            super(BizAttributeType.class);
        }
    }
}
