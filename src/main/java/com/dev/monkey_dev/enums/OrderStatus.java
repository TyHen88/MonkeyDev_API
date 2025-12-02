package com.dev.monkey_dev.enums;

import java.util.Arrays;

public enum OrderStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public static OrderStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.value.equalsIgnoreCase(value))
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
