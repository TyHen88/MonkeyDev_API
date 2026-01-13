package com.dev.monkey_dev.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;
import com.dev.monkey_dev.service.category.ICategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
public class CateController extends BaseApiRestController {

    private final ICategoryService categoryService;

    @Operation(summary = "Create category", description = "Create a new category")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        CategorySummaryDto response = categoryService.createCategory(categoryRequestDto);
        return created(response);
    }

    @Operation(summary = "Get all categories", description = "Get all categories")
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<CategorySummaryDto> response = categoryService.getAllCategories();
        return success(response);
    }

    @Operation(summary = "Get category by ID", description = "Get a category by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        CategorySummaryDto response = categoryService.getCategoryById(id);
        return success(response);
    }

    @Operation(summary = "Update category", description = "Update a category")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.updateCategory(id, categoryRequestDto);
        return successMessage("Category updated successfully");
    }

    @Operation(summary = "Delete category", description = "Delete a category")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return successMessage("Category deleted successfully");
    }

    @Operation(summary = "Remove product from category", description = "Remove a product from a category")
    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<?> removeProductFromCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        categoryService.removeProductFromCategory(categoryId, productId);
        return successMessage("Product removed from category successfully");
    }

    @Operation(summary = "Add product to category", description = "Add a product to a category")
    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<?> addProductToCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        categoryService.addProductToCategory(categoryId, productId);
        return successMessage("Product added to category successfully");
    }

    @Operation(summary = "Bulk delete products from category", description = "Bulk delete products from a category")
    @DeleteMapping("/{categoryId}/products/bulk-delete")
    public ResponseEntity<?> bulkDeleteProductsFromCategory(@PathVariable Long categoryId,
            @RequestBody List<Long> productIds) {
        categoryService.bulkDeleteProductsFromCategory(categoryId, productIds);
        return successMessage("Products deleted from category successfully");
    }

    @Operation(summary = "Bulk add products to category", description = "Bulk add products to a category")
    @PostMapping("/{categoryId}/products/bulk-add")
    public ResponseEntity<?> bulkAddProductsToCategory(@PathVariable Long categoryId,
            @RequestBody List<Long> productIds) {
        categoryService.bulkAddProductsToCategory(categoryId, productIds);
        return successMessage("Products added to category successfully");
    }
}
