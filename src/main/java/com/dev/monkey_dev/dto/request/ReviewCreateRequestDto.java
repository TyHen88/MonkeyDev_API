package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.*;

public record ReviewCreateRequestDto(
    @NotNull Long productId,
    @NotNull Long userId,
    Long orderId,
    @NotNull @Min(1) @Max(5) Integer rating,
    String title,
    String comment
) {}
