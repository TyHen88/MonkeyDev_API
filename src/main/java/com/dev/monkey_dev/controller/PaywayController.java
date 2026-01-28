package com.dev.monkey_dev.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.payload.payway.PaywayPurchaseRequest;
import com.dev.monkey_dev.payload.payway.PaywayVerifyRequest;
import com.dev.monkey_dev.service.payment.PaywayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/payments/payway")
@RequiredArgsConstructor
@Tag(name = "PayWay", description = "ABA PayWay payment integration")
public class PaywayController extends BaseApiRestController {

    private final PaywayService paywayService;

    @PostMapping("/purchase")
    @Operation(summary = "Create PayWay transaction", description = "Creates a PayWay transaction and returns the checkout HTML")
    public ResponseEntity<String> createTransaction(@RequestBody @Valid PaywayPurchaseRequest request) {
        String html = paywayService.createTransaction(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(html);
    }

    @PostMapping("/return")
    @Operation(summary = "PayWay callback", description = "Receive PayWay return_url callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
        paywayService.handleCallback(payload);
        return successMessage("OK");
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify PayWay transaction", description = "Checks the transaction status with PayWay")
    public ResponseEntity<?> verifyTransaction(@RequestBody @Valid PaywayVerifyRequest request) {
        return success(paywayService.verifyTransaction(request.getTranId()));
    }
}
