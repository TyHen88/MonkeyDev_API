package com.dev.monkey_dev.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
    private String role;
    private List<String> roles;
    private List<String> permissions;
    private String profileImageUrl;
    private String authProvider;
    private List<AddressResponseDto> addresses;
}
