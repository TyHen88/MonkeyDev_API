package com.dev.monkey_dev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.AddressRequestDto;
import com.dev.monkey_dev.dto.response.AddressResponseDto;
import com.dev.monkey_dev.service.address.IAddressService;

@RestController
@RequestMapping("/api/wb/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address API")
public class AddressController extends BaseApiRestController {

    private final IAddressService addressService;

    @Operation(summary = "Create address", description = "Create a new address")
    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto response = addressService.createAddress(addressRequestDto);
        return created(response);
    }

    @Operation(summary = "Get address by ID", description = "Get an address by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        AddressResponseDto response = addressService.getAddressById(id);
        return success(response);
    }

    @Operation(summary = "Get address by ID", description = "Get an address by its ID")
    @GetMapping("/all-addresses")
    public ResponseEntity<?> getUsersAddress() {
        var response = addressService.getAllAddresses();
        return success(response);
    }

    @Operation(summary = "Update address", description = "Update an existing address")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto response = addressService.updateAddress(id, addressRequestDto);
        return success(response);
    }

    @Operation(summary = "Delete address", description = "Delete an existing address")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return successMessage("Address deleted successfully");
    }

}
