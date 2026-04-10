package com.ezcart.api.dto.response;

import com.ezcart.api.enums.AddressType;

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
