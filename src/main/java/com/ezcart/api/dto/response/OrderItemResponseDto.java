package com.ezcart.api.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long id,
    Long productId,
    Long productVariationId,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    Object productSnapshot // or JsonNode (recommended)
) {}
