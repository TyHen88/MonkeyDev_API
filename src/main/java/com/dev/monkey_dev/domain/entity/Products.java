package com.dev.monkey_dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_product_slug", columnNames = "slug"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Products extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToMany
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "weight", precision = 10, scale = 2)
    private java.math.BigDecimal weight; // in kg

    @Column(name = "length", precision = 10, scale = 2)
    private java.math.BigDecimal length; // in cm

    @Column(name = "width", precision = 10, scale = 2)
    private java.math.BigDecimal width; // in cm

    @Column(name = "height", precision = 10, scale = 2)
    private java.math.BigDecimal height; // in cm

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private java.math.BigDecimal taxRate; // percentage, e.g. 10.00 for 10%

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Builder.Default
    @Column(name = "is_new", nullable = false)
    private Boolean isNew = false;

}
