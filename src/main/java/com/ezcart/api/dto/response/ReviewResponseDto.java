package com.ezcart.api.dto.response;

public record ReviewResponseDto(
    Long id,
    Long productId,
    Long userId,
    Long orderId,
    Integer rating,
    String title,
    String comment,
    Boolean isVerifiedPurchase,
    Boolean isApproved,
    Integer helpfulCount
) {}
