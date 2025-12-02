package com.dev.monkey_dev.service.impl;

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
import com.dev.monkey_dev.service.users.IAddressService;

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
        address.setUser(userRepository.findById(AuthHelper.getUserId())
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

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        address.setType(addressRequestDto.getType());
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
