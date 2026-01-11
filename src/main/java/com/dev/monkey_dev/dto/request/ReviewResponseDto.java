package com.dev.monkey_dev.dto.request;

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