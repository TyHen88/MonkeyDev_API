package com.dev.monkey_dev.dto.request;

public record ProductImageDto(
    Long id,
    String imageUrl,
    String altText,
    Integer displayOrder,
    Boolean isPrimary
) {}
