package com.dev.monkey_dev.service.seller;

import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.SellerProfileRequestDto;
import com.dev.monkey_dev.dto.request.SellerProfileUpdateRequestDto;
import com.dev.monkey_dev.dto.response.SellerProfileResponseDto;
import com.dev.monkey_dev.domain.entity.SellerProfile;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.SellerProfileRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.enums.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SellerProfileServiceImpl implements SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    @Override
    public SellerProfileResponseDto createSellerProfile(Long userId, SellerProfileRequestDto requestDto) {
        log.info("Creating seller profile for user ID: {}", userId);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        System.err.println("User role:" + user.getRole());

        if (sellerProfileRepository.existsByUser(user)) {
            throw new RuntimeException("Seller profile already exists for user: " + user.getUsername());
        }

        if (user.getRole() != Roles.SELLER) {
            user.setRole(Roles.SELLER);
            userRepository.save(user);
        }

        SellerProfile sellerProfile = SellerProfile.builder()
                .user(user)
                .storeName(requestDto.getStoreName())
                .storeDescription(requestDto.getStoreDescription())
                .storeLogoUrl(requestDto.getStoreLogoUrl())
                .storeBannerUrl(requestDto.getStoreBannerUrl())
                .sellerRating(BigDecimal.ZERO)
                .totalReviews(0L)
                .totalSales(0L)
                .verifiedSeller(false)
                .businessRegistrationNumber(requestDto.getBusinessRegistrationNumber())
                .businessAddress(requestDto.getBusinessAddress())
                .contactPhone(requestDto.getContactPhone())
                .contactEmail(requestDto.getContactEmail())
                .establishedDate(requestDto.getEstablishedDate())
                .returnPolicy(requestDto.getReturnPolicy())
                .shippingPolicy(requestDto.getShippingPolicy())
                .active(true)
                .build();

        SellerProfile savedProfile = sellerProfileRepository.save(sellerProfile);
        log.info("Successfully created seller profile for user ID: {}", userId);

        return mapToResponseDto(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDto getSellerProfileById(Long id) {
        log.info("Fetching seller profile by ID: {}", id);

        SellerProfile sellerProfile = sellerProfileRepository.findActiveSellerProfileById(id)
                .orElseThrow(() -> new RuntimeException("Seller profile not found with ID: " + id));

        return mapToResponseDto(sellerProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDto getSellerProfileByUserId(Long userId) {
        log.info("Fetching seller profile by user ID: {}", userId);

        SellerProfile sellerProfile = sellerProfileRepository.findActiveSellerProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found for user ID: " + userId));

        return mapToResponseDto(sellerProfile);
    }

    @Override
    public SellerProfileResponseDto updateSellerProfile(Long userId, SellerProfileUpdateRequestDto requestDto) {
        log.info("Updating seller profile for user ID: {}", userId);

        SellerProfile sellerProfile = sellerProfileRepository.findActiveSellerProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found for user ID: " + userId));

        if (requestDto.getStoreName() != null)
            sellerProfile.setStoreName(requestDto.getStoreName());
        if (requestDto.getStoreDescription() != null)
            sellerProfile.setStoreDescription(requestDto.getStoreDescription());
        if (requestDto.getStoreLogoUrl() != null)
            sellerProfile.setStoreLogoUrl(requestDto.getStoreLogoUrl());
        if (requestDto.getStoreBannerUrl() != null)
            sellerProfile.setStoreBannerUrl(requestDto.getStoreBannerUrl());
        if (requestDto.getBusinessRegistrationNumber() != null)
            sellerProfile.setBusinessRegistrationNumber(requestDto.getBusinessRegistrationNumber());
        if (requestDto.getBusinessAddress() != null)
            sellerProfile.setBusinessAddress(requestDto.getBusinessAddress());
        if (requestDto.getContactPhone() != null)
            sellerProfile.setContactPhone(requestDto.getContactPhone());
        if (requestDto.getContactEmail() != null)
            sellerProfile.setContactEmail(requestDto.getContactEmail());
        if (requestDto.getReturnPolicy() != null)
            sellerProfile.setReturnPolicy(requestDto.getReturnPolicy());
        if (requestDto.getShippingPolicy() != null)
            sellerProfile.setShippingPolicy(requestDto.getShippingPolicy());

        SellerProfile updatedProfile = sellerProfileRepository.save(sellerProfile);
        log.info("Successfully updated seller profile for user ID: {}", userId);

        return mapToResponseDto(updatedProfile);
    }

    @Override
    public void deleteSellerProfile(Long userId) {
        log.info("Deactivating seller profile for user ID: {}", userId);

        SellerProfile sellerProfile = sellerProfileRepository.findActiveSellerProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found for user ID: " + userId));

        sellerProfile.deactivate();
        sellerProfileRepository.save(sellerProfile);

        Users user = sellerProfile.getUser();
        if (user.getRole() == Roles.SELLER) {
            user.setRole(Roles.USER);
            userRepository.save(user);
        }

        log.info("Successfully deactivated seller profile for user ID: {}", userId);
    }

    @Override
    public Page<SellerProfileResponseDto> getAllSellerProfiles(Boolean isActive, Boolean isVerified,
            CriteriaFilter criteriaFilter) {
        Page<SellerProfile> sellerProfiles = sellerProfileRepository.findAllSellerProfilesWithFilters(
                isActive, isVerified, criteriaFilter.getSearch(),
                criteriaFilter.toPageable("createdAt", Sort.Direction.DESC));

        List<SellerProfileResponseDto> responseDtos = sellerProfiles.getContent()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, sellerProfiles.getPageable(), sellerProfiles.getTotalElements());
    }

    @Override
    public Page<SellerProfileResponseDto> getVerifiedSellers(CriteriaFilter criteriaFilter) {
        Pageable pageable = criteriaFilter.toPageable("sellerRating", Sort.Direction.DESC);
        Page<SellerProfile> sellerProfiles = sellerProfileRepository.findAllSellerProfilesWithFilters(
                true, true, criteriaFilter.getSearch(), pageable);

        List<SellerProfileResponseDto> responseDtos = sellerProfiles.getContent()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, sellerProfiles.getPageable(), sellerProfiles.getTotalElements());
    }

    @Override
    public SellerProfileResponseDto verifySeller(Long sellerProfileId) {
        log.info("Verifying seller profile with ID: {}", sellerProfileId);

        SellerProfile sellerProfile = sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found with ID: " + sellerProfileId));

        sellerProfile.verifySeller();
        SellerProfile verifiedProfile = sellerProfileRepository.save(sellerProfile);

        log.info("Successfully verified seller profile with ID: {}", sellerProfileId);

        return mapToResponseDto(verifiedProfile);
    }

    @Override
    public SellerProfileResponseDto updateSellerRating(Long sellerProfileId, BigDecimal newRating, Long totalReviews) {
        log.info("Updating seller rating for profile ID: {} - new rating: {}, total reviews: {}",
                sellerProfileId, newRating, totalReviews);

        SellerProfile sellerProfile = sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found with ID: " + sellerProfileId));

        sellerProfile.updateRating(newRating, totalReviews);
        SellerProfile updatedProfile = sellerProfileRepository.save(sellerProfile);

        log.info("Successfully updated seller rating for profile ID: {}", sellerProfileId);

        return mapToResponseDto(updatedProfile);
    }

    @Override
    public void incrementSellerSales(Long sellerProfileId) {
        log.info("Incrementing sales for seller profile ID: {}", sellerProfileId);

        SellerProfile sellerProfile = sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found with ID: " + sellerProfileId));

        sellerProfile.incrementSales();
        sellerProfileRepository.save(sellerProfile);

        log.info("Successfully incremented sales for seller profile ID: {}", sellerProfileId);
    }

    private SellerProfileResponseDto mapToResponseDto(SellerProfile sellerProfile) {
        return SellerProfileResponseDto.builder()
                .id(sellerProfile.getId())
                .userId(sellerProfile.getUser().getId())
                .username(sellerProfile.getUser().getUsername())
                .userEmail(sellerProfile.getUser().getEmail())
                .storeName(sellerProfile.getStoreName())
                .storeDescription(sellerProfile.getStoreDescription())
                .storeLogoUrl(sellerProfile.getStoreLogoUrl())
                .storeBannerUrl(sellerProfile.getStoreBannerUrl())
                .sellerRating(sellerProfile.getSellerRating())
                .totalReviews(sellerProfile.getTotalReviews())
                .totalSales(sellerProfile.getTotalSales())
                .verifiedSeller(sellerProfile.isVerifiedSeller())
                .businessRegistrationNumber(sellerProfile.getBusinessRegistrationNumber())
                .businessAddress(sellerProfile.getBusinessAddress())
                .contactPhone(sellerProfile.getContactPhone())
                .contactEmail(sellerProfile.getContactEmail())
                .establishedDate(sellerProfile.getEstablishedDate())
                .returnPolicy(sellerProfile.getReturnPolicy())
                .shippingPolicy(sellerProfile.getShippingPolicy())
                .active(sellerProfile.isActive())
                .verificationDate(sellerProfile.getVerificationDate())
                .createdAt(sellerProfile.getCreatedAt())
                .updatedAt(sellerProfile.getUpdatedAt())
                .build();
    }
}
