package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileRequestDto {

    @NotBlank(message = "Store name is required")
    @Size(max = 200, message = "Store name must not exceed 200 characters")
    private String storeName;

    @Size(max = 1000, message = "Store description must not exceed 1000 characters")
    private String storeDescription;

    @Size(max = 500, message = "Store logo URL must not exceed 500 characters")
    private String storeLogoUrl;

    @Size(max = 500, message = "Store banner URL must not exceed 500 characters")
    private String storeBannerUrl;

    @Size(max = 100, message = "Business registration number must not exceed 100 characters")
    private String businessRegistrationNumber;

    @Size(max = 500, message = "Business address must not exceed 500 characters")
    private String businessAddress;

    @Size(max = 100, message = "Contact phone must not exceed 100 characters")
    private String contactPhone;

    @Size(max = 200, message = "Contact email must not exceed 200 characters")
    private String contactEmail;

    private LocalDateTime establishedDate;

    @Size(max = 1000, message = "Return policy must not exceed 1000 characters")
    private String returnPolicy;

    @Size(max = 1000, message = "Shipping policy must not exceed 1000 characters")
    private String shippingPolicy;
}
