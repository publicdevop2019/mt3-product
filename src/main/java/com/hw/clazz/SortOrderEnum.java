package com.hw.clazz;

import com.hw.shared.BadRequestException;

public enum SortOrderEnum {
    ASC("asc"),
    DESC("desc");
    private String sortOrder;

    SortOrderEnum(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    public static SortOrderEnum fromString(String text) {
        for (SortOrderEnum b : SortOrderEnum.values()) {
            if (b.sortOrder.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new BadRequestException("No constant with text " + text + " found");
    }
}
