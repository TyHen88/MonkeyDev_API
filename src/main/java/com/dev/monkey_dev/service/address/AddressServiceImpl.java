package com.dev.monkey_dev.service.address;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(rollbackFor = Exception.class)
    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        Address address = addressMapper.toAddressEntity(addressRequestDto);
        Long userId = AuthHelper.getUserId();
        address.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND)));
        address = addressRepository.save(address);
        return addressMapper.toAddressResponseDto(address);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AddressResponseDto getAddressById(@NonNull Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        return addressMapper.toAddressResponseDto(address);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<AddressResponseDto> getAllAddresses() {
        Long userId = AuthHelper.getUserId();
        List<Address> addresses = addressRepository.findDefaultAddressByUserId(userId);
        return addresses.stream()
                .map(addressMapper::toAddressResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryAddress(@NonNull Long id) {
        var addresses = addressRepository.findDefaultAddressByUserId(AuthHelper.getUserId());
        addresses.forEach(address -> address.setIsDefault(false));
        addressRepository.saveAll(addresses);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressResponseDto updateAddress(@NonNull Long id, AddressRequestDto addressRequestDto) {
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(@NonNull Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(StatusCode.ADDRESS_NOT_FOUND));
        address.isDeleted();
        addressRepository.save(address);
    }
}
