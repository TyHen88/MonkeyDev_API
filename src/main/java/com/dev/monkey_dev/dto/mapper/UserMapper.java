package com.dev.monkey_dev.dto.mapper;

import org.springframework.stereotype.Component;

import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;

@Component
public class UserMapper {

    /**
     * Maps a UserRequestDto to a Users entity.
     * Returns null if the input is null.
     */
    public Users toUserEntity(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            return null;
        }
        return Users.builder()
                .fullName(userRequestDto.getFullName())
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .active(true)
                .build();
    }

    /**
     * Maps a Users entity to a UserResponseDto.
     * Returns null if the input is null.
     */
    public UserResponseDto toUserResponseDto(Users user) {
        if (user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .build();
    }
}
