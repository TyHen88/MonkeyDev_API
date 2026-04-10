package com.ezcart.api.service.address;

import java.util.List;
import org.springframework.lang.NonNull;
import com.ezcart.api.dto.request.AddressRequestDto;
import com.ezcart.api.dto.response.AddressResponseDto;

public interface IAddressService {
    AddressResponseDto createAddress(AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(@NonNull Long id);

    AddressResponseDto updateAddress(@NonNull Long id, AddressRequestDto addressRequestDto);

    void deleteAddress(@NonNull Long id);

    List<AddressResponseDto> getAllAddresses();

    void setPrimaryAddress(@NonNull Long id);

}

