package com.dev.monkey_dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private String storeName;
    private String storeDescription;
    private String storeLogoUrl;
    private String storeBannerUrl;
    private BigDecimal sellerRating;
    private Long totalReviews;
    private Long totalSales;
    private boolean verifiedSeller;
    private String businessRegistrationNumber;
    private String businessAddress;
    private String contactPhone;
    private String contactEmail;
    private LocalDateTime establishedDate;
    private String returnPolicy;
    private String shippingPolicy;
    private boolean active;
    private LocalDateTime verificationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
