package com.dev.monkey_dev.dto.request;

import com.dev.monkey_dev.enums.AddressType;

public record AddressDto(
    Long id,
    AddressType type,
    String fullName,
    String phone,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String postalCode,
    String country,
    Boolean isDefault
) {}