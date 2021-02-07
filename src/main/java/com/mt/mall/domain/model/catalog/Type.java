package com.mt.mall.domain.model.catalog;

import com.mt.common.persistence.EnumConverter;

public enum Type {
    FRONTEND,
    BACKEND,
    ;

    public static class DBConverter extends EnumConverter<Type> {
        public DBConverter() {
            super(Type.class);
        }
    }
}
