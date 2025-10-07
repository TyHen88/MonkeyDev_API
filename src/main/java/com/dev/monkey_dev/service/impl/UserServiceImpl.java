package com.dev.monkey_dev.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.dto.mapper.UserMapper;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.service.IUserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
        }
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
        }
        Users user = userMapper.toUserEntity(userRequestDto);
        Users savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        Users user = userRepository.findById(id).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmailOrUsername(String email, String username) {
        if (email == null && username == null) {
            throw new BusinessException(StatusCode.EMAIL_REQUIRED);
        }
        Users user = userRepository.findByEmailOrUsername(email, username)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        Users user = userRepository.findById(id).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        if (!user.getEmail().equals(userRequestDto.getEmail())) {
            if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
                throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
            }
        }
        if (!user.getUsername().equals(userRequestDto.getUsername())) {
            if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
                throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
            }
        }

        user.setFullName(userRequestDto.getFullName());
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        Users savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUserIsActive(Boolean isActive) {
        if (isActive == null) {
            throw new BusinessException(StatusCode.IS_ACTIVE_REQUIRED);
        }
        List<UserResponseDto> users = userRepository.findAllUserIsActive(isActive);
        return users
                .stream()
                .collect(Collectors.toList());
    }

}
