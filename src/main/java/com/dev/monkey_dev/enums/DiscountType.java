package com.dev.monkey_dev.enums;

import java.util.Arrays;

public enum DiscountType {
    PERCENTAGE("percentage"),
    FIXED_AMOUNT("fixed_amount");

    private final String value;

    DiscountType(String value) {
        this.value = value;
    }

    public static DiscountType fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(DiscountType.values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    public String getName() {
        return name();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
