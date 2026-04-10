package com.ezcart.api.dto.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.*;

import com.ezcart.api.domain.entity.Address;
import com.ezcart.api.domain.entity.Order;
import com.ezcart.api.domain.entity.OrderItem;
import com.ezcart.api.domain.entity.Payment;
import com.ezcart.api.dto.response.AddressDto;
import com.ezcart.api.dto.response.OrderItemResponseDto;
import com.ezcart.api.dto.response.OrderResponseDto;
import com.ezcart.api.dto.response.PaymentSummaryDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "billingAddress", source = "billingAddress")
    @Mapping(target = "payment", source = "payment")
    @Mapping(target = "items", ignore = true)
    OrderResponseDto toResponse(Order order);

    AddressDto toAddressDto(Address address);
    PaymentSummaryDto toPaymentSummary(Payment payment);

    // Items
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productVariationId", source = "productVariation.id")
    OrderItemResponseDto toItemDto(OrderItem item);

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}

