package com.hw.aggregate.product.model;

import com.hw.shared.EnumDBConverter;

public enum ProductStatus {
    AVAILABLE,
    UNAVAILABLE;

    public static class DBConverter extends EnumDBConverter {
        public DBConverter() {
            super(ProductStatus.class);
        }
    }
}
