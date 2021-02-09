package com.mt.mall.domain.model.product;


import com.mt.common.persistence.EnumConverter;

public enum TagType {
    KEY,
    PROD,
    GEN,
    SALES;

    public static class DBConverter extends EnumConverter<TagType> {
        public DBConverter() {
            super(TagType.class);
        }
    }
}
