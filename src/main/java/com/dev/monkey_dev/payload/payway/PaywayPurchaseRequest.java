package com.dev.monkey_dev.payload.payway;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaywayPurchaseRequest {
    @JsonProperty("req_time")
    private String reqTime;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("tran_id")
    @NotBlank
    private String tranId;

    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    @NotBlank
    private String type;

    @JsonProperty("payment_option")
    @NotBlank
    private String paymentOption;

    // Base64-encoded items JSON (if provided, takes precedence)
    private String items;

    // Optional structured items list to be encoded into "items"
    @JsonProperty("items_list")
    private List<PaywayItem> itemsList;

    private BigDecimal shipping;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @JsonProperty("return_url")
    private String returnUrl;

    @JsonProperty("cancel_url")
    private String cancelUrl;

    @JsonProperty("continue_success_url")
    private String continueSuccessUrl;

    @JsonProperty("custom_fields")
    private String customFields;

    @JsonProperty("return_params")
    private String returnParams;

    private String hash;
}
