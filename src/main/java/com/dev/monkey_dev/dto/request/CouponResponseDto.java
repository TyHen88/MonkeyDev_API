package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.dev.monkey_dev.enums.DiscountType;
public record CouponResponseDto(
    Long id,
    String code,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minPurchaseAmount,
    BigDecimal maxDiscountAmount,
    Integer usageLimit,
    Integer usedCount,
    OffsetDateTime validFrom,
    OffsetDateTime validUntil,
    Boolean isActive
) {}