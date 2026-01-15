package com.dev.monkey_dev.service.seller;

import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.SellerProfileRequestDto;
import com.dev.monkey_dev.dto.request.SellerProfileUpdateRequestDto;
import com.dev.monkey_dev.dto.response.SellerProfileResponseDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface SellerProfileService {

    SellerProfileResponseDto createSellerProfile(Long userId, SellerProfileRequestDto requestDto);

    SellerProfileResponseDto getSellerProfileById(Long id);

    SellerProfileResponseDto getSellerProfileByUserId(Long userId);

    SellerProfileResponseDto updateSellerProfile(Long userId, SellerProfileUpdateRequestDto requestDto);

    void deleteSellerProfile(Long userId);

    Page<SellerProfileResponseDto> getAllSellerProfiles(Boolean isActive, Boolean isVerified, CriteriaFilter criteriaFilter);
//
    Page<SellerProfileResponseDto> getVerifiedSellers(CriteriaFilter criteriaFilter);

    SellerProfileResponseDto verifySeller(Long sellerProfileId);

    SellerProfileResponseDto updateSellerRating(Long sellerProfileId, BigDecimal newRating, Long totalReviews);

    void incrementSellerSales(Long sellerProfileId);
}
