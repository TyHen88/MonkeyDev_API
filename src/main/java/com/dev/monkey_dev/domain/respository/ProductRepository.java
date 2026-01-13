package com.dev.monkey_dev.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

import com.dev.monkey_dev.domain.entity.Products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, Long> {
    // Define repository methods here
    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.categories c WHERE (:categorySlug IS NULL OR c.slug = :categorySlug) AND p.isActive = true")
    Page<Products> findAllByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.slug = :slug AND p.isActive = true")
    Optional<Products> findBySlug(@Param("slug") String slug);

    // remove product from category
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM product_categories WHERE product_id = :productId AND category_id = :categoryId", nativeQuery = true)
    void removeProductFromCategory(@Param("categoryId") Long categoryId, @Param("productId") Long productId);

    // add product to category
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO product_categories (product_id, category_id) VALUES (:productId, :categoryId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addProductToCategory(@Param("categoryId") Long categoryId, @Param("productId") Long productId);

}
