package com.dev.monkey_dev.service.users;

import java.util.List;
import com.dev.monkey_dev.dto.request.AddressRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;

public interface IAddressService {
    AddressResponseDto createAddress(AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(Long id);

    AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto);

    void deleteAddress(Long id);

    // List<AddressResponseDto> getAllAddresses();
}
