package com.dev.monkey_dev.enums;

public enum Roles {
    ADMIN("Administrator"),
    USER("User"),
    SELLER("Seller");

    private final String displayName;

    Roles(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }
}
