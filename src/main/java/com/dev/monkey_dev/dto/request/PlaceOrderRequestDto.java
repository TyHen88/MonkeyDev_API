package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

public record PlaceOrderRequestDto(
    @NotNull Long userId,
    @NotNull Long shippingAddressId,
    @NotNull Long billingAddressId,
    String notes,

    // Optional: if ordering without cart
    List<OrderItemCreateDto> items,

    // Optional coupon usage
    List<Long> couponIds
) {}
