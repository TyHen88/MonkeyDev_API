package com.dev.monkey_dev.service.address;

import java.util.List;
import org.springframework.lang.NonNull;
import com.dev.monkey_dev.dto.request.AddressRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;

public interface IAddressService {
    AddressResponseDto createAddress(AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(@NonNull Long id);

    AddressResponseDto updateAddress(@NonNull Long id, AddressRequestDto addressRequestDto);

    void deleteAddress(@NonNull Long id);

    List<AddressResponseDto> getAllAddresses();

}
