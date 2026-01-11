package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CartResponseDto(
    Long id,
    Long userId,
    List<CartItemResponseDto> items,
    BigDecimal subtotal,
    String currency,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
