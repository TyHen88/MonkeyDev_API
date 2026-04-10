package com.ezcart.api.dto.response;

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
