package com.dev.monkey_dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "seller_profiles", uniqueConstraints = @UniqueConstraint(name = "uk_seller_profile_user", columnNames = "user_id"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SellerProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;

    @Column(name = "store_description", columnDefinition = "TEXT")
    private String storeDescription;

    @Column(name = "store_logo_url", length = 500)
    private String storeLogoUrl;

    @Builder.Default
    @Column(name = "seller_rating", precision = 3, scale = 2)
    private java.math.BigDecimal sellerRating = java.math.BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_sales", nullable = false)
    private Integer totalSales = 0;

    @Builder.Default
    @Column(name = "is_verified_seller", nullable = false)
    private Boolean isVerifiedSeller = false;

    @Column(name = "business_registration_number", length = 100)
    private String businessRegistrationNumber;
}
