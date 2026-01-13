package com.dev.monkey_dev.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.common.PaginatedResponse;
import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;
import com.dev.monkey_dev.service.product.IProductService;

@RestController
@RequestMapping("/api/wb/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API")
public class ProductController extends BaseApiRestController {

    private final IProductService productService;

    @Operation(summary = "Create product", description = "Create a new product")
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequestDto productCreateRequestDto) {
        productService.createProduct(productCreateRequestDto);
        return successMessage("Product created successfully");
    }

    @Operation(summary = "Get product by ID", description = "Get a product by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        ProductResponseDto response = productService.getProductById(id);
        return success(response);
    }

    @Operation(summary = "Get all products", description = "Get all products with optional filters")
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = "categorySlug", required = false) String categorySlug,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        CriteriaFilter criteriaFilter = CriteriaFilter.builder()
                .search(search)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        Page<ProductResponseDto> productsPage = productService.getAllProducts(categorySlug, criteriaFilter);
        Map<String, Object> responseMap = PaginatedResponse.of(productsPage);
        return success(responseMap);
    }

    @Operation(summary = "Update product", description = "Update an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto) {
        productService.updateProduct(id, productUpdateRequestDto);
        return successMessage("Product updated successfully");
    }

    @Operation(summary = "Delete product", description = "Delete an existing product")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return successMessage("Product deleted successfully");
    }

    @Operation(summary = "Get product by slug", description = "Get a product by its slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
        ProductResponseDto response = productService.getProductBySlug(slug);
        return success(response);
    }
}
