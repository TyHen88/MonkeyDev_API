package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemAddRequestDto(
    @NotNull Long productId,
    Long productVariationId,
    @NotNull @Min(1) Integer quantity
) {}
