package com.ezcart.api.service.seller;

import com.ezcart.api.dto.request.CriteriaFilter;
import com.ezcart.api.dto.request.SellerProfileRequestDto;
import com.ezcart.api.dto.request.SellerProfileUpdateRequestDto;
import com.ezcart.api.dto.response.SellerProfileResponseDto;
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

