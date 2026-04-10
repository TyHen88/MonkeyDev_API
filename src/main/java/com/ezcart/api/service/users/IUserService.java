package com.ezcart.api.service.users;

import org.springframework.data.domain.Page;

import com.ezcart.api.dto.request.CriteriaFilter;
import com.ezcart.api.dto.request.UserRequestDto;
import com.ezcart.api.dto.response.UserResponseDto;

public interface IUserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getUserProfile();

    UserResponseDto getUserByEmailOrUsername(String email, String username);

    UserResponseDto updateUser(UserRequestDto userRequestDto);

    void updateUserStatus(Long id, Boolean isActive);

    Page<UserResponseDto> getAllUsers(Boolean isActive, CriteriaFilter criteriaFilter);
}
