package com.dev.monkey_dev.controller.admin;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.UserAdminRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;
import com.dev.monkey_dev.common.PaginatedResponse;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dev.monkey_dev.service.auth.AuthService;
import com.dev.monkey_dev.service.users.IUserService;

@RestController
@RequestMapping("/api/wb/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin Role", description = "Admin Role API")
public class UserAdminController extends BaseApiRestController {
    private final AuthService authService;
    private final IUserService userService;

    /**
     * Get all users with filtering, pagination, and sorting.
     * 
     * @param isActive filter by active status (optional)
     * @param search   search keyword for fullName, username, or email (optional)
     * @param sort     sort field and direction, e.g., "createdAt,desc" or
     *                 "username,asc" (optional)
     * @param page     page number (0-based, default: 0)
     * @param size     page size (default: 10)
     * @return paginated list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users with filtering, pagination, and sorting", description = "Get all users with filtering, pagination, and sorting")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {

        // Build CriteriaFilter from query parameters
        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .search(search)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        // Get paginated users
        Page<UserResponseDto> usersPage = userService.getAllUsers(isActive, criteriaFilter);

        // Build response with data and pagination using utility component
        Map<String, Object> response = PaginatedResponse.of(usersPage);

        return success(response);
    }

    /**
     * Create a new user with role assignment (Admin only).
     * 
     * @param userAdminRequestDto user data with role
     * @return created user
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user with role assignment (Admin only)", description = "Create a new user with role assignment (Admin only)")
    public ResponseEntity<?> createUser(@RequestBody UserAdminRequestDto userAdminRequestDto) {
        try {
            authService.registerUser(userAdminRequestDto);
            return successMessage("User created successfully");
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a user by ID.
     * 
     * @param id user ID
     * @return success message
     */
    @Operation(summary = "true/false", description = "true: activate, false: deactivate")
    @PutMapping("/update-status/{id}/{status}")
    public ResponseEntity<?> updateUserStatus(@PathVariable("id") Long id, @PathVariable("status") Boolean status) {
        userService.updateUserStatus(id, status);
        return successMessage("User status updated successfully");
    }

}
