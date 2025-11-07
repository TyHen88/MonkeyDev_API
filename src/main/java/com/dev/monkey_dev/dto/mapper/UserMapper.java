package com.dev.monkey_dev.dto.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.enums.AuthProvider;
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
                .profileImageUrl(
                        userRequestDto.getProfileImageUrl() == null ? null : userRequestDto.getProfileImageUrl())
                .authProvider(AuthProvider.LOCAL)
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
                .createdAt(user.getCreatedAt() == null
                        ? LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(user.getUpdatedAt() == null
                        ? LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : user.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .role(user.getRole().name())
                .profileImageUrl(user.getProfileImageUrl())
                .authProvider(user.getAuthProvider().name())
                .build();
    }
}
