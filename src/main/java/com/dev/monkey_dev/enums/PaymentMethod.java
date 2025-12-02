package com.dev.monkey_dev.enums;

import java.util.Arrays;

public enum PaymentMethod {
    CREDIT_CARD("credit_card"),
    DEBIT_CARD("debit_card"),
    PAYPAL("paypal"),
    BANK_TRANSFER("bank_transfer"),
    CASH_ON_DELIVERY("cash_on_delivery"),
    DIGITAL_WALLET("digital_wallet"),
    CRYPTOCURRENCY("cryptocurrency");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public static PaymentMethod fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(PaymentMethod.values())
                .filter(method -> method.value.equalsIgnoreCase(value))
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
