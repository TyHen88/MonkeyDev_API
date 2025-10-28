package com.dev.monkey_dev.controller;

import com.dev.monkey_dev.controller.base.BaseApiRestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;
import com.dev.monkey_dev.service.users.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/wb/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController extends BaseApiRestController {

    private final IUserService userService;

    /**
     * Get all users by active status.
     * 
     * @param active true for active users, false for inactive
     * @return list of users with the given active status
     */
    @GetMapping("/active/{active}")
    public ResponseEntity<?> getUsersActive(@PathVariable("active") Boolean active) {
        List<UserResponseDto> users = userService.getAllUserIsActive(active);
        return success(users);
    }

    /**
     * Get user by ID.
     * 
     * @param id user ID
     * @return user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        UserResponseDto user = userService.getUserById(id);
        return success(user);
    }

    /**
     * Create a new user.
     * 
     * @param userRequestDto user data
     * @return created user
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userRequestDto) {
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserRequestDto userRequestDto) {
        userService.updateUser(id, userRequestDto);
        return successMessage("User updated successfully");
    }

    /**
     * Delete a user by ID.
     * 
     * @param id user ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return successMessage("User deleted successfully");
    }

    /**
     * Get user by email or username.
     * 
     * @param email    user's email
     * @param username user's username
     * @return user details
     */
    @GetMapping("/search")
    public ResponseEntity<?> getUserByEmailOrUsername(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username) {
        UserResponseDto user = userService.getUserByEmailOrUsername(email, username);
        return success(user);
    }
}