package com.ezcart.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ezcart.api.common.api.ApiResponse;
import com.ezcart.api.controller.base.BaseApiRestController;
import com.ezcart.api.dto.request.UserRequestDto;
import com.ezcart.api.dto.response.UserResponseDto;
import com.ezcart.api.service.users.IUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@PreAuthorize("isAuthenticated()")
public class UserController extends BaseApiRestController {

    private final IUserService userService;

    /**
     * Get user by ID.
     * 
     * @param id user ID
     * @return user details
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getUserProfile() {
        UserResponseDto user = userService.getUserProfile();
        return success(user);
    }

    /**
     * Create a new user.
     * 
     * @param userRequestDto user data
     * @return created user
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<ApiResponse<Object>> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.createUser(userRequestDto);
        return created(user);
    }

    /**
     * Update an existing user.
     * 
     * @param id             user ID
     * @param userRequestDto updated user data
     * @return updated user
     */
    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<Object>> updateUser(@RequestBody UserRequestDto userRequestDto) {
        userService.updateUser(userRequestDto);
        return successMessage("User updated successfully");
    }

    /**
     * Get user by email or username.
     * 
     * @param email    user's email
     * @param username user's username
     * @return user details
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> getUserByEmailOrUsername(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username) {
        UserResponseDto user = userService.getUserByEmailOrUsername(email, username);
        return success(user);
    }
}
