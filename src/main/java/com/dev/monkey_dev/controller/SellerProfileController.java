package com.dev.monkey_dev.controller;

import com.dev.monkey_dev.common.PaginatedResponse;
import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.SellerProfileRequestDto;
import com.dev.monkey_dev.dto.request.SellerProfileUpdateRequestDto;
import com.dev.monkey_dev.dto.response.SellerProfileResponseDto;
import com.dev.monkey_dev.enums.FilterProductCateType;
import com.dev.monkey_dev.helper.AuthHelper;
import com.dev.monkey_dev.service.seller.SellerProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wb/v1/seller-profiles")
@RequiredArgsConstructor
@Tag(name = "Seller Profile Management", description = "APIs for managing seller profiles")
public class SellerProfileController extends BaseApiRestController {

    private final SellerProfileService sellerProfileService;

    /**
     * Create a new seller profile for the authenticated user.
     * 
     * @param requestDto seller profile data
     * @return created seller profile
     */
    @PostMapping
    @Operation(summary = "Create a new seller profile", description = "Creates a new seller profile for the authenticated user")
    public ResponseEntity<?> createSellerProfile(@Valid @RequestBody SellerProfileRequestDto requestDto) {
        Long userId = AuthHelper.getUserId();

        SellerProfileResponseDto response = sellerProfileService.createSellerProfile(userId, requestDto);
        return created(response);
    }

    /**
     * Get seller profile by ID.
     * 
     * @param id seller profile ID
     * @return seller profile details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get seller profile by ID", description = "Retrieves a seller profile by its ID")
    public ResponseEntity<?> getSellerProfileById(@PathVariable Long id) {
        SellerProfileResponseDto response = sellerProfileService.getSellerProfileById(id);
        return success(response);
    }

    /**
     * Get seller profile by user ID.
     * 
     * @param userId user ID
     * @return seller profile details
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get seller profile by user ID", description = "Retrieves a seller profile by user ID")
    public ResponseEntity<?> getSellerProfileByUserId(@PathVariable Long userId) {
        SellerProfileResponseDto response = sellerProfileService.getSellerProfileByUserId(userId);
        return success(response);
    }

    /**
     * Get current user's seller profile.
     * 
     * @return seller profile details
     */
    @GetMapping("/my-profile")
    @Operation(summary = "Get current user's seller profile", description = "Retrieves the seller profile of the authenticated user")
    public ResponseEntity<?> getMySellerProfile() {
        Long userId = AuthHelper.getUserId();

        SellerProfileResponseDto response = sellerProfileService.getSellerProfileByUserId(userId);
        return success(response);
    }

    /**
     * Update seller profile for the authenticated user.
     * 
     * @param requestDto updated seller profile data
     * @return success message
     */
    @PutMapping
    @Operation(summary = "Update seller profile", description = "Updates the seller profile of the authenticated user")
    public ResponseEntity<?> updateSellerProfile(@Valid @RequestBody SellerProfileUpdateRequestDto requestDto) {
        Long userId = AuthHelper.getUserId();

        sellerProfileService.updateSellerProfile(userId, requestDto);
        return successMessage("Seller profile updated successfully");
    }

    /**
     * Deactivate seller profile for the authenticated user.
     * 
     * @return success message
     */
    @DeleteMapping
    @Operation(summary = "Delete seller profile", description = "Deactivates the seller profile of the authenticated user")
    public ResponseEntity<?> deleteSellerProfile() {
        Long userId = AuthHelper.getUserId();

        sellerProfileService.deleteSellerProfile(userId);
        return successMessage("Seller profile deactivated successfully");
    }


    @GetMapping
    @Operation(summary = "Get all seller profiles", description = "Retrieves all seller profiles with pagination")
    public ResponseEntity<?> getAllSellerProfiles(
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "isVerified", required = false) Boolean isVerified,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {

        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .search(search)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        Page<SellerProfileResponseDto> response = sellerProfileService.getAllSellerProfiles(isActive, isVerified,
                criteriaFilter);
        Map<String, Object> responseMap = PaginatedResponse.of(response);
        return success(responseMap);
    }

    /**
     * Get verified sellers with pagination.
     * 
     * @param page page number
     * @param size page size
     * @return paginated verified sellers
     */
//    @GetMapping("/verified")
//    @Operation(summary = "Get verified sellers", description = "Retrieves all verified seller profiles with pagination")
//    public ResponseEntity<?> getVerifiedSellers(
//            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
//
//        PaginatedResponse<SellerProfileResponseDto> response = sellerProfileService.getVerifiedSellers(page, size);
//        return success(response);
//    }

    /**
     * Verify a seller profile (Admin only).
     * 
     * @param id seller profile ID
     * @return verified seller profile
     */
    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify seller", description = "Verifies a seller profile (Admin only)")
    public ResponseEntity<?> verifySeller(@PathVariable Long id) {
        SellerProfileResponseDto response = sellerProfileService.verifySeller(id);
        return success(response);
    }

    /**
     * Update seller rating (Admin only).
     * 
     * @param id           seller profile ID
     * @param newRating    new rating value
     * @param totalReviews total review count
     * @return updated seller profile
     */
    @PutMapping("/{id}/rating")
    @Operation(summary = "Update seller rating", description = "Updates the seller rating and total reviews (Admin only)")
    public ResponseEntity<?> updateSellerRating(
            @PathVariable Long id,
            @RequestParam BigDecimal newRating,
            @RequestParam Long totalReviews) {

        SellerProfileResponseDto response = sellerProfileService.updateSellerRating(id, newRating, totalReviews);
        return success(response);
    }

    /**
     * Increment seller sales count (Admin only).
     * 
     * @param id seller profile ID
     * @return success message
     */
    @PostMapping("/{id}/increment-sales")
    @Operation(summary = "Increment seller sales", description = "Increments the total sales count for a seller (Admin only)")
    public ResponseEntity<?> incrementSellerSales(@PathVariable Long id) {
        sellerProfileService.incrementSellerSales(id);
        return successMessage("Seller sales incremented successfully");
    }
}
