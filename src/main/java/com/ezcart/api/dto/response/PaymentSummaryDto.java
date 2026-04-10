package com.ezcart.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.ezcart.api.enums.PaymentMethod;
import com.ezcart.api.enums.PaymentStatus;

public record PaymentSummaryDto(
    Long id,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus,
    String transactionId,
    BigDecimal amount,
    String currency,
    OffsetDateTime paymentDate
) {}
