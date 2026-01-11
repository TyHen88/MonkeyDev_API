package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;

public record CartItemResponseDto(
    Long id,
    Long productId,
    Long productVariationId,
    String productTitle,
    String variationLabel,
    Integer quantity,
    BigDecimal priceAtAdd,
    BigDecimal lineTotal
) {}