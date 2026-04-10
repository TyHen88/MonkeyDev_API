package com.ezcart.api.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.ezcart.api.domain.entity.ProductVariation;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.product.id = :productId ORDER BY pv.id ASC")
    List<ProductVariation> findByProductId(@Param("productId") Long productId);
}

