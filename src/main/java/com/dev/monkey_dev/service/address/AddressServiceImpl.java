package com.dev.monkey_dev.service.address;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.domain.respository.AddressRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.dto.mapper.AddressMapper;
import com.dev.monkey_dev.dto.request.AddressRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;
import com.dev.monkey_dev.enums.AddressType;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.helper.AuthHelper;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final AddressMapper addressMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        Address address = addressMapper.toAddressEntity(addressRequestDto);
        Long userId = AuthHelper.getUserId();
        address.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND)));
        address = addressRepository.save(address);
        return addressMapper.toAddressResponseDto(address);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        return addressMapper.toAddressResponseDto(address);
    }

    public List<AddressResponseDto> getAllAddresses() {
        Long userId = AuthHelper.getUserId();
        List<Address> addresses = addressRepository.findDefaultAddressByUserId(userId);
        return addresses.stream()
                .map(addressMapper::toAddressResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        address.setType(AddressType.valueOf(addressRequestDto.getType().toUpperCase()));
        address.setFullName(addressRequestDto.getFullName());
        address.setPhone(addressRequestDto.getPhone());
        address.setAddressLine1(addressRequestDto.getAddressLine1());
        address.setAddressLine2(addressRequestDto.getAddressLine2());
        address.setCity(addressRequestDto.getCity());
        address.setState(addressRequestDto.getState());
        address.setPostalCode(addressRequestDto.getPostalCode());
        address.setCountry(addressRequestDto.getCountry());
        address.setIsDefault(addressRequestDto.getIsDefault());
        address = addressRepository.save(address);
        return addressMapper.toAddressResponseDto(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        address.deactivate();
        addressRepository.save(address);
    }
}
