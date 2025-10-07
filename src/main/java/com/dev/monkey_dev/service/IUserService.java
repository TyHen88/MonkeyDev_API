package com.dev.monkey_dev.service;

import java.util.List;

import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;

public interface IUserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto getUserByEmailOrUsername(String email, String username);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    void deleteUser(Long id);

    List<UserResponseDto> getAllUserIsActive(Boolean isActive);
}