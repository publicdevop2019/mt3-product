package com.hw.aggregate.product.model;

import com.hw.shared.EnumDBConverter;

public enum TagTypeEnum {
    KEY,
    PROD,
    GEN,
    SALES;

    public static class DBConverter extends EnumDBConverter<TagTypeEnum> {
        public DBConverter() {
            super(TagTypeEnum.class);
        }
    }
}
