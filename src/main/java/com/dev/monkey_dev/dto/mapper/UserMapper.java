package com.dev.monkey_dev.dto.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.domain.entity.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.dev.monkey_dev.enums.AuthProvider;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.request.UserAdminRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;
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
     * Maps a UserAdminRequestDto to a Users entity without role assignment.
     * Role assignment is handled in the service layer.
     */
    public Users toUserEntity(UserAdminRequestDto userAdminRequestDto) {
        if (userAdminRequestDto == null) {
            return null;
        }
        Boolean active = userAdminRequestDto.getActive() != null
                ? userAdminRequestDto.getActive()
                : true;

        return Users.builder()
                .fullName(userAdminRequestDto.getFullName())
                .username(userAdminRequestDto.getUsername())
                .active(active)
                .build();
    }

    public AddressResponseDto toAddressResponseDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressResponseDto.builder()
                .id(address.getId())
                .type(address.getType().name())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

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
                .role(primaryRole(user))
                .roles(user.getRoles() == null ? List.of() : user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toList()))
                .permissions(user.getRoles() == null ? List.of() : user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getName())
                        .distinct()
                        .collect(Collectors.toList()))
                .profileImageUrl(user.getProfileImageUrl())
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().name() : null)
                .build();
    }

    /**
     * Maps a Users entity to a UserResponseDto.
     * Returns null if the input is null.
     */
    public UserResponseDto toUsersResponseDto(Users user, List<Address> addresses) {
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
                .role(primaryRole(user))
                .roles(user.getRoles() == null ? List.of() : user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toList()))
                .permissions(user.getRoles() == null ? List.of() : user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getName())
                        .distinct()
                        .collect(Collectors.toList()))
                .profileImageUrl(user.getProfileImageUrl())
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().name() : null)
                .addresses(null == addresses ? new ArrayList<>()
                        : addresses.stream()
                                .map(this::toAddressResponseDto)
                                .collect(Collectors.toList()))
                .build();
    }

    private String primaryRole(Users user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return null;
        }
        return user.getRoles().iterator().next().getName();
    }
}
