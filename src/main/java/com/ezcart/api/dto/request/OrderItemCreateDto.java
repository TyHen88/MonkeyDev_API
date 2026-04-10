package com.ezcart.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateDto(
    @NotNull Long productId,
    Long productVariationId,
    @NotNull @Min(1) Integer quantity
) {}
