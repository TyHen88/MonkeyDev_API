package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDto(
    @NotBlank String name,
    @NotBlank String slug,
    @NotBlank String description,
    @NotBlank String imageUrl,
    @NotNull Boolean isActive,
    Long parentId
) {}
