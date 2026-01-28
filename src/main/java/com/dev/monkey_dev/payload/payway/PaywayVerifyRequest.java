package com.dev.monkey_dev.payload.payway;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaywayVerifyRequest {
    @JsonProperty("tran_id")
    @NotBlank
    private String tranId;
}
