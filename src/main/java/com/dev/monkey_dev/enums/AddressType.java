package com.dev.monkey_dev.enums;

import java.util.Arrays;

public enum AddressType {
    SHIPPING("shipping"),
    BILLING("billing"),
    BOTH("both");

    private final String value;

    AddressType(String value) {
        this.value = value;
    }

    public static AddressType fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(AddressType.values())
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
