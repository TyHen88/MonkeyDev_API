package com.dev.monkey_dev.service.users;

import org.springframework.data.domain.Page;

import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;

public interface IUserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getUserProfile();

    UserResponseDto getUserByEmailOrUsername(String email, String username);

    UserResponseDto updateUser(UserRequestDto userRequestDto);

    void updateUserStatus(Long id, Boolean isActive);

    Page<UserResponseDto> getAllUsers(Boolean isActive, CriteriaFilter criteriaFilter);
}