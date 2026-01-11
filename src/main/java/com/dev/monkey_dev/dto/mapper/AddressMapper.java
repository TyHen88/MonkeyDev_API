package com.dev.monkey_dev.dto.mapper;

import org.springframework.stereotype.Component;

import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.dto.request.AddressRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;
import com.dev.monkey_dev.enums.AddressType;

@Component
public class AddressMapper {
    public Address toAddressEntity(AddressRequestDto addressRequestDto) {
        if (addressRequestDto == null) {
            return null;
        }
        return Address.builder()
                .type(AddressType.valueOf(addressRequestDto.getType().toUpperCase()))
                .fullName(addressRequestDto.getFullName())
                .phone(addressRequestDto.getPhone())
                .addressLine1(addressRequestDto.getAddressLine1())
                .addressLine2(addressRequestDto.getAddressLine2())
                .city(addressRequestDto.getCity())
                .state(addressRequestDto.getState())
                .postalCode(addressRequestDto.getPostalCode())
                .country(addressRequestDto.getCountry())
                .isDefault(addressRequestDto.getIsDefault())
                .build();
    }

    public AddressResponseDto toAddressResponseDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressResponseDto.builder()
                .id(address.getId())
                .type(address.getType().name())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
