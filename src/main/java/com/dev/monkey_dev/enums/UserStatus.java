package com.dev.monkey_dev.enums;

import java.util.Arrays;

/**
 * Enum representing the status of a user.
 */
public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    /**
     * Constructor for UserStatus.
     * 
     * @param value the string value to match
     */
    UserStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the UserStatus corresponding to the given string value,
     * case-insensitive.
     * 
     * @param value the string value to match
     * @return the matching UserStatus, or null if not found
     */
    public static UserStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(UserStatus.values())
                .filter(userStatus -> userStatus.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return this.value;
    }
}
