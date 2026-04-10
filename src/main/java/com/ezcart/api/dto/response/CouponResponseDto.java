package com.ezcart.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.ezcart.api.enums.DiscountType;
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
