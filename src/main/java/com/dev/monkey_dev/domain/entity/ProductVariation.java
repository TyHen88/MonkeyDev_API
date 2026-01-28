package com.dev.monkey_dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_variations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProductVariation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g. "Size", "Color"

    @Column(name = "value", nullable = false, length = 100)
    private String value; // e.g. "Large", "Red"

    @Column(name = "price_adjustment", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal priceAdjustment; // e.g. 10.00

    @Column(name = "sku", length = 100)
    private String sku; // e.g. "1234567890" or "RED-L-10"

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity; // e.g. 100

}
