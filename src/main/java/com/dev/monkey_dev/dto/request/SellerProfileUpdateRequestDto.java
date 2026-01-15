package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileUpdateRequestDto {

    @NotNull(message = "Seller profile ID is required")
    private Long id;

    private String storeName;
    private String storeDescription;
    private String storeLogoUrl;
    private String storeBannerUrl;
    private String businessRegistrationNumber;
    private String businessAddress;
    private String contactPhone;
    private String contactEmail;
    private String returnPolicy;
    private String shippingPolicy;
}
