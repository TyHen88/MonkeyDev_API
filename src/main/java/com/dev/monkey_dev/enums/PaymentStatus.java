package com.dev.monkey_dev.enums;

import java.util.Arrays;

public enum PaymentStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed"),
    REFUNDED("refunded"),
    CANCELLED("cancelled");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public static PaymentStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(PaymentStatus.values())
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
