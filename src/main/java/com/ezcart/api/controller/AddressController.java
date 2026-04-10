package com.ezcart.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import com.ezcart.api.common.api.ApiResponse;
import com.ezcart.api.controller.base.BaseApiRestController;
import com.ezcart.api.dto.request.AddressRequestDto;
import com.ezcart.api.dto.response.AddressResponseDto;
import com.ezcart.api.service.address.IAddressService;

@RestController
@RequestMapping("/api/wb/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address API")
@PreAuthorize("isAuthenticated()")
public class AddressController extends BaseApiRestController {

    private final IAddressService addressService;

    @Operation(summary = "Create address", description = "Create a new address")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createAddress(@RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto response = addressService.createAddress(addressRequestDto);
        return created(response);
    }

    @Operation(summary = "Get address by ID", description = "Get an address by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getAddressById(@PathVariable Long id) {
        AddressResponseDto response = addressService.getAddressById(id);
        return success(response);
    }

    @Operation(summary = "Get all user addresses", description = "Get an address")
    @GetMapping("/all-addresses")
    public ResponseEntity<ApiResponse<Object>> getUsersAddress() {
        var response = addressService.getAllAddresses();
        return success(response);
    }

    @Operation(summary = "Update address", description = "Update an existing address")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAddress(@PathVariable Long id, @RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto response = addressService.updateAddress(id, addressRequestDto);
        return success(response);
    }

    @Operation(summary = "Delete address", description = "Delete an existing address")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return successMessage("Address deleted successfully");
    }

    @Operation(summary = "Set primary address", description = "Set an address as primary for the user")
    @PatchMapping("/{id}/is-primary")
    public ResponseEntity<ApiResponse<Object>> isPrimaryAddress(@PathVariable Long id) {
        addressService.setPrimaryAddress(id);
        return successMessage("Address set as primary successfully");
    }

}

